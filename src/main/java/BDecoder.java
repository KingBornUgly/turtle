import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class BDecoder {
    // 字符集
    public static final String BYTE_ENCODING = "UTF8";
    public static Charset BYTE_CHARSET;

    static {
        try {
            BYTE_CHARSET = Charset.forName(BYTE_ENCODING);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static final boolean TRACE = true;

    private boolean recovery_mode;

    public static Map decode(BufferedInputStream is) throws Exception { // 解码
        return (new BDecoder().decodeStream(is));
    }

    public BDecoder() {
    }

    public Map decodeStream(BufferedInputStream data) throws Exception {
        Object res = decodeInputStream(new BDecoderInputStreamStream(data), 0); // 0指定递归层次从第一层开始
        if (res == null) {
            throw (new Exception(" BDecoder: zero length file "));

        } else if (!(res instanceof Map)) {
            throw (new Exception(" BDecoder: top level isn't a Map "));
        }
        return ((Map) res);
    }

    /**
     * @param dbis
     * @param nesting 递归层次
     * @throws Exception
     */
    private Object decodeInputStream(BDecoderInputStream dbis, int nesting) throws Exception {
        if (nesting == 0 && !dbis.markSupported()) {
            throw new IOException(" InputStream must support the mark() method ");
        }
        // set a mark
        dbis.mark(Integer.MAX_VALUE);

        // read a byte
        int tempByte = dbis.read(); // 读一个字节

        // decide what to do
        switch (tempByte) {
            case 'd': { // 是字典
                // create a new dictionary object
                Map tempMap = new HashMap();
                try {
                    // get the key
                    byte[] tempByteArray = null;
                    while ((tempByteArray = (byte[]) decodeInputStream(dbis, nesting + 1)) != null) {
                        // decode some more
                        Object value = decodeInputStream(dbis, nesting + 1); // 读值
                        //  value interning is too CPU-intensive, let's skip that for now
                        // if(value instanceof byte[] && ((byte[])value).length < 17)
                        // value = StringInterner.internBytes((byte[])value);
                        //  keys often repeat a lot - intern to save space
                        String key = null;

                        if (key == null) {
                            CharBuffer cb = BYTE_CHARSET.decode(ByteBuffer.wrap(tempByteArray));
                            key = new String(cb.array(), 0, cb.limit()); // 键
                        }
                        if (TRACE) {
                            System.out.println(key + " -> " + value + " ; ");
                        }

                        //  recover from some borked encodings that I have seen whereby the value has
                        //  not been encoded. This results in, for example,
                        //  18:azureus_propertiesd0:e
                        //  we only get null back here if decoding has hit an 'e' or end-of-file
                        //  that is, there is no valid way for us to get a null 'value' here

                        if (value == null) {
                            // Debug.out( "Invalid encoding - value not serialsied for '" + key + "' - ignoring" );
                            break;
                        }
                        tempMap.put(key, value); // 放入结果集中
                    }

                    dbis.mark(Integer.MAX_VALUE);
                    tempByte = dbis.read();
                    dbis.reset();
                    if (nesting > 0 && tempByte == -1) {
                        throw (new Exception(" BDecoder: invalid input data, 'e' missing from end of dictionary "));
                    }
                } catch (Throwable e) {
                    if (!recovery_mode) {
                        if (e instanceof IOException) {
                            throw ((IOException) e);
                        }

                        throw (new IOException(e.getMessage()));
                    }
                }
                return tempMap;
            }
            case 'l': {
                // create the list
                ArrayList tempList = new ArrayList();
                try {
                    // create the key
                    Object tempElement = null;
                    while ((tempElement = decodeInputStream(dbis, nesting + 1)) != null) {
                        // add the element
                        tempList.add(tempElement); // 读取列表元素并加入列表中
                    }

                    tempList.trimToSize();
                    dbis.mark(Integer.MAX_VALUE);
                    tempByte = dbis.read();
                    dbis.reset();
                    if (nesting > 0 && tempByte == -1) {
                        throw (new Exception(" BDecoder: invalid input data, 'e' missing from end of list "));
                    }
                } catch (Throwable e) {
                    if (!recovery_mode) {
                        if (e instanceof IOException) {
                            throw ((IOException) e);
                        }
                        throw (new IOException(e.getMessage()));
                    }
                }
                // return the list
                return tempList;
            }
            case 'e':
            case -1:
                return null; // 当前结束

            case 'i':
                return new Long(getNumberFromStream(dbis, 'e')); // 整数

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                // move back one
                dbis.reset();
                // get the string
                return getByteArrayFromStream(dbis); // 读取指定长度字符串

            default: {
                int rem_len = dbis.available();
                if (rem_len > 256) {
                    rem_len = 256;
                }

                byte[] rem_data = new byte[rem_len];
                dbis.read(rem_data);
                throw (new Exception(" BDecoder: unknown command ' " + tempByte + " , remainder =  " + new String(rem_data)));
            }
        }
    }

    /**
     * only create the array once per decoder instance (no issues with recursion as it's only used in a leaf method)
     */
    private final char[] numberChars = new char[32];

    private long getNumberFromStream(BDecoderInputStream dbis, char parseChar) throws IOException {
        int tempByte = dbis.read();
        int pos = 0;
        while ((tempByte != parseChar) && (tempByte >= 0)) { // 读取整数字节，直到终结字符'e'
            numberChars[pos++] = (char) tempByte;
            if (pos == numberChars.length) {
                throw (new NumberFormatException(" Number too large:  " + new String(numberChars, 0, pos) + "  "));
            }
            tempByte = dbis.read();
        }

        // are we at the end of the stream?
        if (tempByte < 0) {
            return -1;
        } else if (pos == 0) {
            //  support some borked impls that sometimes don't bother encoding anything
            return (0);
        }
        return (parseLong(numberChars, 0, pos)); // 转换为Long型整数
    }

    public static long parseLong(char[] chars, int start, int length) { // 转换为Long型整数
        long result = 0;
        boolean negative = false;
        int i = start;
        int max = start + length;
        long limit;
        if (length > 0) {
            if (chars[i] == '-') {
                negative = true;
                limit = Long.MIN_VALUE;
                i++;
            } else {
                limit = -Long.MAX_VALUE;
            }

            if (i < max) {
                int digit = chars[i++] - '0';
                if (digit < 0 || digit > 9) {
                    throw new NumberFormatException(new String(chars, start, length));
                } else {
                    result = -digit;
                }
            }

            long multmin = limit / 10;

            while (i < max) {
                //  Accumulating negatively avoids surprises near MAX_VALUE
                int digit = chars[i++] - '0';
                if (digit < 0 || digit > 9) {
                    throw new NumberFormatException(new String(chars, start, length));
                }
                if (result < multmin) {
                    throw new NumberFormatException(new String(chars, start, length));
                }
                result *= 10;
                if (result < limit + digit) {
                    throw new NumberFormatException(new String(chars, start, length));
                }

                result -= digit;
            }
        } else {
            throw new NumberFormatException(new String(chars, start, length));
        }

        if (negative) {
            if (i > start + 1) {
                return result;

            } else {     /*  Only got "-"  */
                throw new NumberFormatException(new String(chars, start, length));
            }
        } else {
            return -result;
        }
    }

    private byte[] getByteArrayFromStream(BDecoderInputStream dbis) throws IOException {
        int length = (int) getNumberFromStream(dbis, ':');
        if (length < 0) {
            return null;
        }
        //  note that torrent hashes can be big (consider a 55GB file with 2MB pieces
        //  this generates a pieces hash of 1/2 meg
        if (length > 8 * 1024 * 1024) {
            throw new IOException(" Byte array length too large ( " + length + " ) ");
        }

        byte[] tempArray = new byte[length];
        int count = 0;
        int len = 0;
        // get the string
        while (count != length && (len = dbis.read(tempArray, count, length - count)) > 0) {
            count += len;
        }
        if (count != tempArray.length) {
            throw new IOException(" BDecoder::getByteArrayFromStream: truncated ");
        }
        return tempArray;
    }

    public void setRecoveryMode(boolean r) {
        recovery_mode = r;
    }

    public static void print(PrintWriter writer, Object obj) {
        print(writer, obj, "", false);
    }

    private static void print(PrintWriter writer, Object obj, String indent, boolean skip_indent) {
        String use_indent = skip_indent ? "" : indent;
        if (obj instanceof Long) {
            writer.println(use_indent + obj);

        } else if (obj instanceof byte[]) {
            byte[] b = (byte[]) obj;
            if (b.length == 20) {
                writer.println(use_indent + b);
            } else if (b.length < 64) {
                writer.println(new String(b));
            } else {
                writer.println(" [byte array length  " + b.length);
            }
        } else if (obj instanceof String) {
            writer.println(use_indent + obj);

        } else if (obj instanceof List) {
            List l = (List) obj;
            writer.println(use_indent + " [ ");
            for (int i = 0; i < l.size(); i++) {
                writer.print(indent + "   ( " + i + " )  ");
                print(writer, l.get(i), indent + "      ", true);
            }
            writer.println(indent + " ] ");

        } else {
            Map m = (Map) obj;
            Iterator it = m.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.length() > 256) {
                    writer.print(indent + key.substring(0, 256) + "  =  ");
                } else {
                    writer.print(indent + key + "  =  ");
                }
                print(writer, m.get(key), indent + "    ", true);
            }
        }
    }

    private static void print(File f, File output) {
        try {
            BDecoder decoder = new BDecoder(); // 解码器
            PrintWriter pw = new PrintWriter(new FileWriter(output)); // 输出结果
            print(pw, decoder.decodeStream(new BufferedInputStream(new FileInputStream(f))));
            pw.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private interface BDecoderInputStream {
        public int read() throws IOException;

        public int read(byte[] buffer) throws IOException;

        public int read(byte[] buffer, int offset, int length) throws IOException;

        public int available() throws IOException;

        public boolean markSupported();

        public void mark(int limit);

        public void reset() throws IOException;
    }

    private class BDecoderInputStreamStream implements BDecoderInputStream {
        final private BufferedInputStream is;

        private BDecoderInputStreamStream(BufferedInputStream _is) {
            is = _is;
        }

        /**
         * 从此输入流中读取下一个数据字节。返回一个 0 到 255 范围内的 int 字节值。
         * 如果因为已经到达流末尾而没有字节可用，则返回 -1。
         * 在输入数据可用、检测到流末尾或抛出异常之前，此方法将一直阻塞。
         */
        public int read() throws IOException {
            return (is.read());
        }

        /**
         * 从此输入流中将 byte.length 个字节的数据读入一个 byte 数组中。在某些输入可用之前，此方法将阻塞。
         */
        public int read(byte[] buffer) throws IOException {
            return (is.read(buffer));
        }

        /**
         * 从此字节输入流中给定偏移量处开始将各字节读取到指定的 byte 数组中。
         */
        public int read(byte[] buffer, int offset, int length) throws IOException {
            return (is.read(buffer, offset, length));
        }

        /**
         * 返回可以从此输入流读取（或跳过）、且不受此输入流接下来的方法调用阻塞的估计字节数。
         */
        public int available() throws IOException {
            return (is.available());
        }

        /**
         * 测试此输入流是否支持 mark 和 reset 方法。
         */
        public boolean markSupported() {
            return (is.markSupported());
        }

        /**
         * 在输入流中的当前位置上作标记。reset 方法的后续调用将此流重新定位在最后标记的位置上，以便后续读取操作重新读取相同的字节。
         *
         * @param limit 在标记位置变为无效之前可以读取字节的最大限制。
         */
        public void mark(int limit) {
            is.mark(limit);
        }

        /**
         * 将此流重新定位到对此输入流最后调用 mark 方法时的位置。
         */
        public void reset() throws IOException {
            is.reset();
        }
    }

    public static void main(String[] args) throws Exception{
//        URL url = new URL("http://tracker1.itzmx.com:8080/announce/The.Blood.of.Youth.S01E13.2022.2160p.WEB-DL.H265.AAC-BlackTV.mp4");
//        URLConnection connection = url.openConnection();
//        InputStream stream = connection.getInputStream();
//        FileOutputStream fileOutputStream = new FileOutputStream("/Users/KingBornUgly/Downloads/load.txt");
//        byte[] data = new byte[1024];
//        int lenth = 0;
//        while((lenth = stream.read(data))!=-1) {
//            fileOutputStream.write(data,0,lenth);
//        }
//        print(new File("/Users/KingBornUgly/Desktop/The.Blood.of.Youth.S01.2022.2160p.WEB-DL.H265.AAC-BlackTV[BTBTT].torrent"), new File("/Users/KingBornUgly/Downloads/load.torrent"));

// 设置下载文件
//        MultifileEventAdapter.files(MultifileSelectorWrapper.newEncoder(list).serialize());
// 添加下载
//        TURTLE.download(torrentPath);
// 等待下载完成
//        TURTLE.lockDownload();
    }
}
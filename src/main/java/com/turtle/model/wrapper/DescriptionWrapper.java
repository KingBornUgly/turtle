package com.turtle.model.wrapper;

import com.turtle.exception.PacketSizeException;
import com.turtle.fromat.BEncodeDecoder;
import com.turtle.fromat.BEncodeEncoder;
import com.turtle.utils.CollectionUtils;
import com.turtle.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>下载描述包装器</p>
 * 
 * @author turtle
 */
public final class DescriptionWrapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionWrapper.class);

	/**
	 * <p>编码器</p>
	 */
	private final BEncodeEncoder encoder;
	/**
	 * <p>解码器</p>
	 */
	private final BEncodeDecoder decoder;

	/**
	 * @param encoder 编码器
	 * @param decoder 解码器
	 */
	private DescriptionWrapper(BEncodeEncoder encoder, BEncodeDecoder decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
	}

	/**
	 * <p>新建下载描述包装器</p>
	 * 
	 * @param list 选择文件列表
	 * 
	 * @return {@link DescriptionWrapper}
	 */
	public static final DescriptionWrapper newEncoder(List<String> list) {
		BEncodeEncoder encoder;
		if(CollectionUtils.isNotEmpty(list)) {
			encoder = BEncodeEncoder.newInstance()
				.newList()
				.put(list);
		} else {
			encoder = null;
		}
		return new DescriptionWrapper(encoder, null);
	}
	
	/**
	 * <p>新建下载描述包装器</p>
	 * 
	 * @param value 选择文件列表（B编码）
	 * 
	 * @return {@link DescriptionWrapper}
	 */
	public static final DescriptionWrapper newDecoder(String value) {
		BEncodeDecoder decoder;
		if(StringUtils.isNotEmpty(value)) {
			decoder = BEncodeDecoder.newInstance(value);
		} else {
			decoder = null;
		}
		return new DescriptionWrapper(null, decoder);
	}
	
	/**
	 * <p>编码选择文件</p>
	 * 
	 * @return 选择文件列表（B编码）
	 */
	public String serialize() {
		if(this.encoder == null) {
			return null;
		}
		return encoder.flush().toString();
	}

	/**
	 * <p>解析选择文件</p>
	 * 
	 * @return 选择文件列表
	 */
	public List<String> deserialize() {
		if(this.decoder == null) {
			return new ArrayList<>();
		}
		try {
			return this.decoder.nextList().stream()
				.filter(Objects::nonNull)
				.map(StringUtils::getString)
				.collect(Collectors.toList());
		} catch (PacketSizeException e) {
			LOGGER.error("解析选择文件异常", e);
		}
		return new ArrayList<>();
	}

}

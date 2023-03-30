/**
 * Copyright (c) 2019, zhonglian . All rights reserved. Use is subject to license terms.
 */
package com.fresh.xy.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseEntity<ID> implements Serializable {

	private static final long serialVersionUID = 4864980818782455146L;
	
	//雪花策略
	@TableId
	private ID id;

	/** mysql-connector驱动包中默认使用SimpleDateFormat[yyyy-MM-dd][yyyy-MM-dd HH:mm:ss]将日期转化成String */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  //json序列化与反序列化的格式化器
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  //spring中日期格式化器
	//private Date createTime;
	private LocalDateTime createTime;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	//private Date modifyTime;
	private LocalDateTime modifyTime;

}

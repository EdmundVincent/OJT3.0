package com.collaboportal.common.utils;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequestParameter implements Serializable {	
	
	// ページ行数
	@NotNull
	protected Integer limit;
	// オフセット
	@NotNull
	protected Integer offset;

}

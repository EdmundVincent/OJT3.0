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
	
	@NotNull
	protected Integer limit;
	@NotNull
	protected Integer offset;

}

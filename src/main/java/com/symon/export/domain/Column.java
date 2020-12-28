package com.symon.export.domain;

import lombok.Data;

@Data
public class Column {

    private Integer ordinal;

    private String name;

    private String dataType;

    private String allowNull;

    private String defaultValue;

    private String comment;
}

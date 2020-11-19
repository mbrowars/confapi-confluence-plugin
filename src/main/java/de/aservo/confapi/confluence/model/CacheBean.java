package de.aservo.confapi.confluence.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
//TODO: add Cache to commons
@XmlRootElement(name = "Cache")
public class CacheBean {

    @NotNull
    @XmlElement
    private String name;

    @XmlElement
    private Integer maxObjectSize;

    @XmlElement
    private Long currentHeapSizeInByte;

    @XmlElement
    private Double effectivenessInPercent;

    @XmlElement
    private Double utilisationInPercent;

    @XmlElement
    private Boolean flushable;

}

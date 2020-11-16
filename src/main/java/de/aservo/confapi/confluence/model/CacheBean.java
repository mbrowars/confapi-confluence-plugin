package de.aservo.confapi.confluence.model;

import de.aservo.confapi.commons.constants.ConfAPI;
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
    private int size;

    @XmlElement
    private String currentHeapSize;

    @XmlElement
    private String effectivenessInPercent;

    @XmlElement
    private String utilisationInPercent;

}

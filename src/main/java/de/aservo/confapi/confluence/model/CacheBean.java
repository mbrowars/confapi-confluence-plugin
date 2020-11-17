package de.aservo.confapi.confluence.model;

import com.atlassian.cache.CacheStatisticsKey;
import de.aservo.confapi.commons.constants.ConfAPI;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.SortedMap;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
//TODO: add Cache to commons
@XmlRootElement(name = "Cache")
public class CacheBean {

    @NotNull
    @XmlElement
    private String name;

    @XmlElement
    private Integer size;

    @XmlElement
    private long currentHeapSize;

    @XmlElement
    private double effectivenessInPercent;

    @XmlElement
    private Double utilisationInPercent;

}

package com.exampletenpo.calculate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Audited
@Table(name = "percentage")
@Entity
public class Percentage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(nullable = false)
    private BigDecimal percentage;

    @Column(nullable = false)
    private BigDecimal firstNumber;

    @Column(nullable = false)
    private BigDecimal secondNumber;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLastUpdate;
}

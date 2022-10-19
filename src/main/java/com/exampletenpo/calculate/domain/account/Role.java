package com.exampletenpo.calculate.domain.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Audited
@EqualsAndHashCode(callSuper = false)
@Table(name = "authorities")
@Entity
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(length = 20, unique = true)
    private String authority;
}

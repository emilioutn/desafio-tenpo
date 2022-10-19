package com.exampletenpo.calculate.domain.history;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "history")
@Entity
public class History implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.AUTO)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(updatable = false, nullable = false)
    private String method;

    @Column(updatable = false, nullable = false)
    private String path;

    @Column(updatable = false, nullable = false)
    private String username;

    @Column(updatable = false, nullable = false)
    private String ipFrom;

    @Column(updatable = false, nullable = false)
    private Integer status;

    @Column(updatable = false, columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private String result;

    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @PrePersist
    private void setDate() {
        date = new Date();
    }
}



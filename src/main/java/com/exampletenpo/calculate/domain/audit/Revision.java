package com.exampletenpo.calculate.domain.audit;

import com.exampletenpo.calculate.config.audit.CustomRevisionListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@RevisionEntity(CustomRevisionListener.class)
@Table(name = "revision_info")
@Entity
public class Revision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_seq")
    @SequenceGenerator(
            name = "revision_seq",
            sequenceName = "public.seq_revision_id"
    )
    @RevisionNumber
    private int id;

    @Column(name = "revision_date")
    @Temporal(TemporalType.TIMESTAMP)
    @RevisionTimestamp
    private Date date;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "form_ip")
    private String fromIp;
}

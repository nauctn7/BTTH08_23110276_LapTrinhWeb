package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(length = 500, columnDefinition = "nvarchar(500) not null")
    private String productName;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private double unitPrice;
    @Column(length = 200)
    private String images;
    @Column(columnDefinition = "nvarchar(500) not null")
    private String description;
    @Column(nullable = false)
    private double discount;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "YYYY-MM-DD hh:mi:ss")
    private Date createDate;
    @Column(nullable = false)
    private short status;
   //@JsonIgnore
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Category category;
    // Quan hệ 1 sản phẩm có nhiều orderDetails
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    private Set<OrderDetailEntity> orderDetails;
}

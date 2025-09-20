package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.Set;

import org.hibernate.annotations.Nationalized;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long categoryId;
//
//    @Column(name = "category_name", length = 200, columnDefinition = "nvarchar(200) not null")
//    private String name;
//
//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
//    private Set<ProductEntity> products;
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;
	@Nationalized
	 @Column(name = "category_name", length = 200, nullable = false)
	private String categoryName;
	private String icon;
	@JsonIgnore
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL )
	private Set<Product> products;

	
	
}

package smartspace.dao.rdb;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class IdGenerator {
	private Long nextId;

	public IdGenerator() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getNextId() {
		return nextId;
	}

	public void setNextId(Long nextId) {
		this.nextId = nextId;
	}
}

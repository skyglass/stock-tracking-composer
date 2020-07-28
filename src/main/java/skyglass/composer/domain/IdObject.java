package skyglass.composer.domain;

import java.io.Serializable;

public interface IdObject extends Serializable {

	public String getUuid();

	public void setUuid(String uuid);
}

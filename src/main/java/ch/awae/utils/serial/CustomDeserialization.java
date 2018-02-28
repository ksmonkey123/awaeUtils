package ch.awae.utils.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public interface CustomDeserialization extends Serializable {
	void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException;

}

package ch.awae.utils.serial;

import java.io.IOException;
import java.io.ObjectOutputStream;

public interface CustomSerialization extends CustomDeserialization {

	void writeObject(ObjectOutputStream out) throws IOException;

}

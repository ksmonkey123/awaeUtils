package ch.awae.utils.serial;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Marks a class as serializable with custom serialization and deserialization.
 * 
 * @see CustomDeserialization
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.1
 */
public interface CustomSerialization extends CustomDeserialization {

    void writeObject(ObjectOutputStream out) throws IOException;

}

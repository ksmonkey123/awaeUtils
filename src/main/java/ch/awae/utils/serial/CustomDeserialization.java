package ch.awae.utils.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Marks a class as serializable with custom deserialization.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.1
 */
public interface CustomDeserialization extends Serializable {

    void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException;

}

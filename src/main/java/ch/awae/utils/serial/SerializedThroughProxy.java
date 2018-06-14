package ch.awae.utils.serial;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Marks a class as serializable through the use of a proxy object.
 * 
 * Whenever an instance of the class is serialized it is replaced by a proxy
 * object. Usually this proxy object should be a {@link SerializationProxy}.
 * 
 * @see SerializationProxy
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.1
 */
public interface SerializedThroughProxy extends Serializable {

    Object writeReplace() throws ObjectStreamException;

}

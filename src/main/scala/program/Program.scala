package program

import scala.util.{Using, Try}
import scala.collection.immutable.ArraySeq

import java.io

import exceptions.{BrokenSerializedProgramFileError, FileNotFoundError}


/** System of Saturn rules and tasks (to which the rules must be applied),
 *  representing Saturn program.
 *
 *  Instances of this class are created using the [[compilation.compileProgram]] method
 *  and are executed using the [[SaturnVirtualMachine.execute]] method.
 *
 *  @param rules A collection of Saturn rules that define the logic of the program.
 *  @param tasks A collection of tasks that the program's rules are applied to.
 *
 *  @see [[Rule]] for details on rule representation.
 *  @see [[Task]] for details on task representation.
*/
@SerialVersionUID(108024218808227L)
case class Program(rules: ArraySeq[Rule], tasks: ArraySeq[Task]) extends java.io.Serializable {

    /** Attempts to write a serialized `Program` object to a specified file.
     *
     *  This method serializes the current `Program` instance and writes it to the given file.
     *  It handles any `java.io.IOException` that might occur during the process.
     *
     *  @param path A path to file to which the serialized object will be written.
     *  @throws java.io.IOException when problems with filesystem.
     *  @note throwing errors such java.io.InvalidClassException unexpected.
     */
    def saveToFile(path: String): Unit = {
        val oper = Using(io.ObjectOutputStream(io.FileOutputStream(path)))
        oper(writer => writer.writeObject(this)).get
    }
}

/** Program Factory. Provides reading from file method. */
object Program {

    /** Attempts to read a serialized `Program` object from a specified file.
     *
     *  This method reads a serialized `Program` instance from the given file and deserializes it.
     *  It handles any `java.io.IOException` that might occur during the process.
     *
     *  @param path A Path to file containing the serialized `Program` object.
     *  @return A Saturn program object
     *  @throws exceptions.BrokenSerializedProgramFileError when the data in the given file can't be deserialized.
     *  @throws java.io.IOException when problems with filesystem.
     *  @throws exceptions.FileNotFoundError when failed to find file.
     */
    def readFromFile(path: String): Program = try {
        val oper = Using(io.ObjectInputStream(io.FileInputStream(path)))
        oper(_.readObject()).get.asInstanceOf[Program]
    } catch {
        case ex: ClassNotFoundException      => throw new BrokenSerializedProgramFileError(path)
        case ex: io.InvalidClassException    => throw new BrokenSerializedProgramFileError(path)
        case ex: io.StreamCorruptedException => throw new BrokenSerializedProgramFileError(path)
        case ex: io.OptionalDataException    => throw new BrokenSerializedProgramFileError(path)
    }
}

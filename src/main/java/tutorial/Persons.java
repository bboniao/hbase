package tutorial;

import org.hbase.generated.AddressBookProtos;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by bboniao on 11/18/14.
 */
public class Persons {

    public static final AddressBookProtos.AddressBook.Builder addressBook = AddressBookProtos.AddressBook.newBuilder();

    static {
        AddressBookProtos.Person.Builder person1 = AddressBookProtos.Person.newBuilder();
        AddressBookProtos.Person.PhoneNumber.Builder phoneNumber1 =
                AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("18920750780").setType(AddressBookProtos.Person.PhoneType.MOBILE);
        person1.addPhone(phoneNumber1);
        person1.setId(1);
        person1.setName("naruto");
        person1.setEmail("bboniao@gmail.com");

        AddressBookProtos.Person.Builder person2 = AddressBookProtos.Person.newBuilder();
        AddressBookProtos.Person.PhoneNumber.Builder phoneNumber2 =
                AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("18657604196").setType(AddressBookProtos.Person.PhoneType.MOBILE);
        person2.addPhone(phoneNumber2);
        person2.setId(2);
        person2.setName("gaara");
        person2.setEmail("bboniao@163.com");

        addressBook.addPerson(person1);
        addressBook.addPerson(person2);
    }
}

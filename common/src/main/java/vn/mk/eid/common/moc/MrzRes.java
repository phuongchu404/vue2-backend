package vn.mk.eid.common.moc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MrzRes {
    String idNumber;
    String name;
    String dateOfBirth;
    String validTo;
    String gender;
}

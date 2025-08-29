package vn.mk.eid.common.icao;


import lombok.Data;
import vn.mk.eid.common.util.StringUtil;

@Data
public class VNIDDG13File {

   private String idCardNo;
   private String name;
   private String dateOfBirth;
   private String gender;
   private String nationality;
   private String ethnic;
   private String religion;
   private String placeOfOrigin;
   private String residenceAddress;
   private String personalSpecificIdentification;
   private String dateOfIssuance;
   private String dateOfExpiry;
   private String motherName;
   private String fatherName;
   private String spouseName;
   private String oldIdCardNumber;
   private String chipId;

   public String buildString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(idCardNo);
      stringBuffer.append(name);
      stringBuffer.append(dateOfBirth);
      stringBuffer.append(gender);
      stringBuffer.append(nationality);
      stringBuffer.append(StringUtil.toEmptyString(ethnic));
      stringBuffer.append(StringUtil.toEmptyString(religion));
      stringBuffer.append(StringUtil.toEmptyString(placeOfOrigin));
      stringBuffer.append(StringUtil.toEmptyString(residenceAddress));
      stringBuffer.append(StringUtil.toEmptyString(personalSpecificIdentification));
      stringBuffer.append(dateOfIssuance);
      stringBuffer.append(dateOfExpiry);
      stringBuffer.append(StringUtil.toEmptyString(motherName));
      stringBuffer.append(StringUtil.toEmptyString(fatherName));
      stringBuffer.append(StringUtil.toEmptyString(spouseName));
      stringBuffer.append(StringUtil.toEmptyString(oldIdCardNumber));
      return stringBuffer.toString();
   }
}

package vn.mk.eid.common.moc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MocRes {
    String signature;
    ArrayList<String> faces = new ArrayList<>();
    ArrayList<String> lifs = new ArrayList<>();
    ArrayList<String> rifs = new ArrayList<>();
}

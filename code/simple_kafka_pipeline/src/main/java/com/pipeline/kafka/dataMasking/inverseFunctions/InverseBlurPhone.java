package com.pipeline.kafka.dataMasking.inverseFunctions;

import com.pipeline.kafka.dataMasking.InverseMaskingFunction;

import java.util.ArrayList;
import java.util.List;

public class InverseBlurPhone implements InverseMaskingFunction {
    public static List<String> eval(String blurredPhone) {
        if (blurredPhone.matches("\\d+")) {
            return List.of(blurredPhone);
        }

        int phoneLen = blurredPhone.length();
        int cut = blurredPhone.indexOf('X');
        String basePhone = blurredPhone.substring(0, cut);
        assert basePhone.isEmpty() || basePhone.matches("\\d+");
        assert blurredPhone.substring(cut).equals("X".repeat(phoneLen - cut));

        List<String> values = new ArrayList<>();
        for (int i = 0; i < Math.pow(10, phoneLen - cut); i++) {
            String tail = String.format("%0" + (phoneLen - cut) + "d", i);
            values.add(basePhone + tail);
        }
        return values;
    }
}

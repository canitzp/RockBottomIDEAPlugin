package de.canitzp.rockbottomdev;

import com.intellij.psi.PsiElement;

import java.util.Arrays;

/**
 * @author canitzp
 */
public class Util {

    public static String getPackageNameOfContainingClass(PsiElement element){
        for(String s : element.getText().split("\n")){
            if(s.startsWith("package")){
                return s.replace("package ", "").replace(";", "");
            }
        }
        return "";
    }

}

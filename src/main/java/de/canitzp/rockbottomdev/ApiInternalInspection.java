package de.canitzp.rockbottomdev;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author canitzp
 */
public class ApiInternalInspection extends BaseInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Wrong RockBottom Api usage";
    }

    @NotNull
    @Override
    protected String buildErrorString(Object... objects) {
        if(objects.length == 1 && objects[0] instanceof Integer){
            int i = (int) objects[0];
            switch (i){
                case 0: return "The object you wanna register has a register() method. Use it instead of directly calling the registry";
                case 1: return "You should never use ApiInternal stuff";
            }
        }
        return "";
    }

    @Override
    public BaseInspectionVisitor buildVisitor() {
        return new BaseInspectionVisitor() {

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                if(!Util.getPackageNameOfContainingClass(expression.getContainingFile()).startsWith("de.ellpeck.rockbottom")){
                    PsiMethod psiMethod = expression.resolveMethod();
                    PsiExpression[] expressions = expression.getArgumentList().getExpressions();
                    boolean foundRegisterClass = false;
                    if(expressions.length >= 2){
                        if(expressions[1].getType() instanceof PsiClassType){
                            PsiClassType type = (PsiClassType) expressions[1].getType();
                            if(type != null && type.resolve() != null){
                                PsiMethod[] methodsFound = Objects.requireNonNull(type.resolve()).findMethodsByName("register", true);
                                if(methodsFound.length > 0){
                                    foundRegisterClass = true;
                                }

                            }
                        }
                    }
                    if(psiMethod != null && "register".equals(psiMethod.getName()) && PsiType.VOID.equals(psiMethod.getReturnType()) && foundRegisterClass){
                        PsiClass psiClass = psiMethod.findSuperMethods()[0].getContainingClass();
                        if(psiClass != null && "de.ellpeck.rockbottom.api.util.reg.IRegistry".equals(psiClass.getQualifiedName())){
                            this.registerError(expression, ProblemHighlightType.WEAK_WARNING, 0);
                        }
                    }
                }
                super.visitMethodCallExpression(expression);
            }

            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                if (!Util.getPackageNameOfContainingClass(expression.getContainingFile()).startsWith("de.ellpeck.rockbottom")) {
                    PsiElement element = expression.resolve();
                    if (element instanceof PsiModifierListOwner) {
                        for (PsiAnnotation annotation : ((PsiModifierListOwner) element).getAnnotations()) {
                            if ("de.ellpeck.rockbottom.api.util.ApiInternal".equals(annotation.getQualifiedName())) { // de.ellpeck.rockbottom.api.util.ApiInternal
                                this.registerError(expression, ProblemHighlightType.LIKE_DEPRECATED, 1);
                            }
                        }
                    }
                }
                super.visitReferenceExpression(expression);
            }

        };
    }

}

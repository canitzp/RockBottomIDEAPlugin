package de.canitzp.rockbottomdev;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author canitzp
 */
public class ApiInternalInspection extends BaseInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "ApiInternal used at wrong place";
    }

    @NotNull
    @Override
    protected String buildErrorString(Object... objects) {
        return "You should never use ApiInternal stuff";
    }

    @Override
    public BaseInspectionVisitor buildVisitor() {
        return new BaseInspectionVisitor() {

            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                PsiElement element = expression.resolve();
                if(element instanceof PsiModifierListOwner){
                    if(Util.getPackageNameOfContainingClass(expression.getContainingFile()).startsWith("de.ellpeck.rockbottom")){
                        return;
                    }
                    for(PsiAnnotation annotation : ((PsiModifierListOwner) element).getAnnotations()){
                        if("de.ellpeck.rockbottom.api.util.ApiInternal".equals(annotation.getQualifiedName())){ // de.ellpeck.rockbottom.api.util.ApiInternal
                            this.registerError(expression, ProblemHighlightType.LIKE_DEPRECATED);
                        }
                    }
                }
                super.visitReferenceExpression(expression);
            }

        };
    }

}

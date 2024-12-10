package io.telicent.jena.abac.attributes.syntax;

import io.telicent.jena.abac.attributes.VisitorAttrExpr;
import io.telicent.jena.abac.core.CxtABAC;
import org.apache.jena.atlas.lib.NotImplemented;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestAE_Var {

    private final CxtABAC mockContext = mock(CxtABAC.class);

    @Test
    public void test_eval_exception() {
        AE_Var var = new AE_Var("test");
        assertThrows(NotImplemented.class, () -> {
            var.eval(mockContext);
        });
    }

    @Test
    public void test_visitor() {
        AE_Var var = new AE_Var("test");
        VisitorAttrExpr mockVistorAttrExpr = mock(VisitorAttrExpr.class);
        var.visitor(mockVistorAttrExpr);
        verify(mockVistorAttrExpr).visit(any(AE_Var.class));
    }

    @Test
    public void test_equals_same() {
        AE_Var var = new AE_Var("test");
        assertTrue(var.equals(var)); // we are specifically testing the equals method here
    }

    @Test
    public void test_equals_identical() {
        AE_Var var1 = new AE_Var("test");
        AE_Var var2 = new AE_Var("test");
        assertTrue(var1.equals(var2)); // we are specifically testing the equals method here
    }

    @Test
    public void test_equals_null() {
        AE_Var var = new AE_Var("test");
        assertFalse(var.equals(null)); // we are specifically testing the equals method here
    }

    @Test
    public void test_equals_different_class() {
        AE_Var var = new AE_Var("test");
        assertFalse(var.equals("test")); // we are specifically testing the equals method here
    }

}

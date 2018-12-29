package kroonprins.mocker.templating.jinjava;

import com.hubspot.jinjava.lib.fn.ELFunctionDefinition;

// TODO can be moved into the JinjavaFunction interface with default stuff?
public abstract class AbstractJinjavaFunction implements JinjavaFunction {

    private static final String NAMESPACE = "";

    protected abstract String getName();

    protected abstract String getMethod();

    protected abstract Class[] getParameterTypes();

    @Override
    public ELFunctionDefinition create() {
        return new ELFunctionDefinition(NAMESPACE, getName(), this.getClass(), getMethod(), getParameterTypes());
    }
}

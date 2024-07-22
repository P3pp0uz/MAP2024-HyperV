package di.uniba.map.b.adventure.type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;


public class Command {

    private final CommandType type;

    private final String name;

    private Set<String> alias;

     private Pattern pattern;

    public Command(CommandType type, String name) {
        this.type = type;
        this.name = name;
    }

    public Command(CommandType type, String name, Set<String> alias) {
        this.type = type;
        this.name = name;
        this.alias = alias;
    }

    /**
     * Getter del nome del comando
     * @return nome del comando
     */
    public String getName() {
        return name;
    }

    /**
     * Getter degli alias del comando
     * @param set degli alias del comando
     */
    public Set<String> getAlias() {
        return alias;
    }

    /**
     * Setter degli alias del comando
     * @param alias alias del comando
     */
    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    /**
     * Setter degli alias del comando
     * @param alias alias del comando
     */
    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
    }

    /**
     * Getter di un tipo di comando
     * @param type tipo di comando
     */
    public CommandType getType() {
        return type;
    }


    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Command other = (Command) obj;
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

}

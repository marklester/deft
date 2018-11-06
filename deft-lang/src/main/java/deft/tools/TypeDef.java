package deft.tools;

import java.util.List;

/**
 * Info about a type
 *
 */
public class TypeDef {
	private final String name;
	private final List<FieldDef> fields;

	public TypeDef(String name, List<FieldDef> fields) {
		this.name = name;
		this.fields = fields;
	}

	public String getName() {
		return name;
	}

	public List<FieldDef> getFields() {
		return fields;
	}
}

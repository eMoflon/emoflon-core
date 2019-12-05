package org.moflon.emf.codegen.template;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.emf.codegen.ecore.genmodel.GenBase;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenOperation;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenParameter;
import org.eclipse.emf.codegen.ecore.genmodel.GenRuntimePlatform;
import org.eclipse.emf.codegen.util.CodeGenUtil;

public class JavaClassGenerator {
	protected static String nl;

	public static synchronized JavaClassGenerator create(final String lineSeparator) {
		nl = lineSeparator;
		JavaClassGenerator result = new JavaClassGenerator();
		nl = null;
		return result;
	}

	public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;

	protected final String TEXT_1 = "/**";

	protected final String TEXT_2 = NL + " * ";

	protected final String TEXT_3 = NL + " */";

	protected final String TEXT_4 = NL + "package ";

	protected final String TEXT_5 = ";";

	protected final String TEXT_6 = NL + "package ";

	protected final String TEXT_7 = ";";

	protected final String TEXT_8 = NL;

	protected final String TEXT_9 = NL;

	protected final String TEXT_10 = NL + "/**" + NL + " * <!-- begin-user-doc -->" + NL
			+ " * A representation of the model object '<em><b>";

	protected final String TEXT_11 = "</b></em>'." + NL + " * <!-- end-user-doc -->";

	protected final String TEXT_12 = NL + " *" + NL + " * <!-- begin-model-doc -->" + NL + " * ";

	protected final String TEXT_13 = NL + " * <!-- end-model-doc -->";

	protected final String TEXT_14 = NL + " *";

	protected final String TEXT_15 = NL + " * <p>" + NL + " * The following features are supported:" + NL + " * <ul>";

	protected final String TEXT_16 = NL + " *   <li>{@link ";

	protected final String TEXT_17 = "#";

	protected final String TEXT_18 = " <em>";

	protected final String TEXT_19 = "</em>}</li>";

	protected final String TEXT_20 = NL + " * </ul>" + NL + " * </p>";

	protected final String TEXT_21 = NL + " *";

	protected final String TEXT_22 = NL + " * @see ";

	protected final String TEXT_23 = "#get";

	protected final String TEXT_24 = "()";

	protected final String TEXT_25 = NL + " * @model ";

	protected final String TEXT_26 = NL + " *        ";

	protected final String TEXT_27 = NL + " * @model";

	protected final String TEXT_28 = NL + " * @extends ";

	protected final String TEXT_29 = NL + " * @generated" + NL + " */";

	protected final String TEXT_30 = NL + "/**" + NL + " * <!-- begin-user-doc -->" + NL
			+ " * An implementation of the model object '<em><b>";

	protected final String TEXT_31 = "</b></em>'." + NL + " * <!-- end-user-doc -->" + NL + " * <p>";

	protected final String TEXT_32 = NL + " * The following features are implemented:" + NL + " * <ul>";

	protected final String TEXT_33 = NL + " *   <li>{@link ";

	protected final String TEXT_34 = "#";

	protected final String TEXT_35 = " <em>";

	protected final String TEXT_36 = "</em>}</li>";

	protected final String TEXT_37 = NL + " * </ul>";

	protected final String TEXT_38 = NL + " * </p>" + NL + " *" + NL + " * @generated" + NL + " */";

	protected final String TEXT_39 = NL + "public";

	protected final String TEXT_40 = " abstract";

	protected final String TEXT_41 = " class ";

	protected final String TEXT_42 = NL + "public interface ";

	protected final String TEXT_43 = NL + "{";

	protected final String TEXT_44 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\t";

	protected final String TEXT_45 = " copyright = ";

	protected final String TEXT_46 = ";";

	protected final String TEXT_47 = NL;

	protected final String TEXT_48 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic static final ";

	protected final String TEXT_49 = " mofDriverNumber = \"";

	protected final String TEXT_50 = "\";";

	protected final String TEXT_51 = NL;

	protected final String TEXT_52 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL
			+ "\tprivate static final long serialVersionUID = 1L;" + NL;

	protected final String TEXT_53 = NL + "\t/**" + NL
			+ "\t * An array of objects representing the values of non-primitive features." + NL
			+ "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL
			+ "\t */";

	protected final String TEXT_54 = NL + "\t@";

	protected final String TEXT_55 = NL + "\tprotected Object[] ";

	protected final String TEXT_56 = ";" + NL;

	protected final String TEXT_57 = NL + "\t/**" + NL
			+ "\t * A bit field representing the indices of non-primitive feature values." + NL
			+ "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL
			+ "\t */";

	protected final String TEXT_58 = NL + "\t@";

	protected final String TEXT_59 = NL + "\tprotected int ";

	protected final String TEXT_60 = ";" + NL;

	protected final String TEXT_61 = NL + "\t/**" + NL
			+ "\t * A set of bit flags representing the values of boolean attributes and whether unsettable features have been set."
			+ NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL
			+ "\t * @ordered" + NL + "\t */";

	protected final String TEXT_62 = NL + "\t@";

	protected final String TEXT_63 = NL + "\tprotected int ";

	protected final String TEXT_64 = " = 0;" + NL;

	protected final String TEXT_65 = NL + "\t/**" + NL + "\t * The cached setting delegate for the '{@link #";

	protected final String TEXT_66 = "() <em>";

	protected final String TEXT_67 = "</em>}' ";

	protected final String TEXT_68 = "." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @see #";

	protected final String TEXT_69 = "()" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */";

	protected final String TEXT_70 = NL + "\t@";

	protected final String TEXT_71 = NL + "\tprotected ";

	protected final String TEXT_72 = ".Internal.SettingDelegate ";

	protected final String TEXT_73 = "__ESETTING_DELEGATE = ((";

	protected final String TEXT_74 = ".Internal)";

	protected final String TEXT_75 = ").getSettingDelegate();" + NL;

	protected final String TEXT_76 = NL + "\t/**" + NL + "\t * The cached value of the '{@link #";

	protected final String TEXT_77 = "() <em>";

	protected final String TEXT_78 = "</em>}' ";

	protected final String TEXT_79 = "." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @see #";

	protected final String TEXT_80 = "()" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */";

	protected final String TEXT_81 = NL + "\t@";

	protected final String TEXT_82 = NL + "\tprotected ";

	protected final String TEXT_83 = " ";

	protected final String TEXT_84 = ";" + NL;

	protected final String TEXT_85 = NL + "\t/**" + NL + "\t * The empty value for the '{@link #";

	protected final String TEXT_86 = "() <em>";

	protected final String TEXT_87 = "</em>}' array accessor." + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @see #";

	protected final String TEXT_88 = "()" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */";

	protected final String TEXT_89 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_90 = NL + "\tprotected static final ";

	protected final String TEXT_91 = "[] ";

	protected final String TEXT_92 = "_EEMPTY_ARRAY = new ";

	protected final String TEXT_93 = " [0]";

	protected final String TEXT_94 = ";" + NL;

	protected final String TEXT_95 = NL + "\t/**" + NL + "\t * The default value of the '{@link #";

	protected final String TEXT_96 = "() <em>";

	protected final String TEXT_97 = "</em>}' ";

	protected final String TEXT_98 = "." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @see #";

	protected final String TEXT_99 = "()" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */";

	protected final String TEXT_100 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_101 = NL + "\tprotected static final ";

	protected final String TEXT_102 = " ";

	protected final String TEXT_103 = "; // TODO The default value literal \"";

	protected final String TEXT_104 = "\" is not valid.";

	protected final String TEXT_105 = " = ";

	protected final String TEXT_106 = ";";

	protected final String TEXT_107 = NL;

	protected final String TEXT_108 = NL + "\t/**" + NL
			+ "\t * An additional set of bit flags representing the values of boolean attributes and whether unsettable features have been set."
			+ NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL
			+ "\t * @ordered" + NL + "\t */";

	protected final String TEXT_109 = NL + "\t@";

	protected final String TEXT_110 = NL + "\tprotected int ";

	protected final String TEXT_111 = " = 0;" + NL;

	protected final String TEXT_112 = NL + "\t/**" + NL
			+ "\t * The offset of the flags representing the value of the '{@link #";

	protected final String TEXT_113 = "() <em>";

	protected final String TEXT_114 = "</em>}' ";

	protected final String TEXT_115 = "." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @generated" + NL + "\t * @ordered" + NL + "\t */" + NL + "\tprotected static final int ";

	protected final String TEXT_116 = "_EFLAG_OFFSET = ";

	protected final String TEXT_117 = ";" + NL + "" + NL + "\t/**" + NL
			+ "\t * The flags representing the default value of the '{@link #";

	protected final String TEXT_118 = "() <em>";

	protected final String TEXT_119 = "</em>}' ";

	protected final String TEXT_120 = "." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @generated" + NL + "\t * @ordered" + NL + "\t */" + NL + "\tprotected static final int ";

	protected final String TEXT_121 = "_EFLAG_DEFAULT = ";

	protected final String TEXT_122 = ".ordinal()";

	protected final String TEXT_123 = ".VALUES.indexOf(";

	protected final String TEXT_124 = ")";

	protected final String TEXT_125 = " << ";

	protected final String TEXT_126 = "_EFLAG_OFFSET;" + NL + "" + NL + "\t/**" + NL
			+ "\t * The array of enumeration values for '{@link ";

	protected final String TEXT_127 = " ";

	protected final String TEXT_128 = "}'" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->"
			+ NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */" + NL + "\tprivate static final ";

	protected final String TEXT_129 = "[] ";

	protected final String TEXT_130 = "_EFLAG_VALUES = ";

	protected final String TEXT_131 = ".values()";

	protected final String TEXT_132 = "(";

	protected final String TEXT_133 = "[])";

	protected final String TEXT_134 = ".VALUES.toArray(new ";

	protected final String TEXT_135 = "[";

	protected final String TEXT_136 = ".VALUES.size()])";

	protected final String TEXT_137 = ";" + NL;

	protected final String TEXT_138 = NL + "\t/**" + NL + "\t * The flag";

	protected final String TEXT_139 = " representing the value of the '{@link #";

	protected final String TEXT_140 = "() <em>";

	protected final String TEXT_141 = "</em>}' ";

	protected final String TEXT_142 = "." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @see #";

	protected final String TEXT_143 = "()" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */" + NL
			+ "\tprotected static final int ";

	protected final String TEXT_144 = "_EFLAG = ";

	protected final String TEXT_145 = " << ";

	protected final String TEXT_146 = "_EFLAG_OFFSET";

	protected final String TEXT_147 = ";" + NL;

	protected final String TEXT_148 = NL + "\t/**" + NL + "\t * The cached value of the '{@link #";

	protected final String TEXT_149 = "() <em>";

	protected final String TEXT_150 = "</em>}' ";

	protected final String TEXT_151 = "." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @see #";

	protected final String TEXT_152 = "()" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */";

	protected final String TEXT_153 = NL + "\t@";

	protected final String TEXT_154 = NL + "\tprotected ";

	protected final String TEXT_155 = " ";

	protected final String TEXT_156 = " = ";

	protected final String TEXT_157 = ";" + NL;

	protected final String TEXT_158 = NL + "\t/**" + NL
			+ "\t * An additional set of bit flags representing the values of boolean attributes and whether unsettable features have been set."
			+ NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL
			+ "\t * @ordered" + NL + "\t */";

	protected final String TEXT_159 = NL + "\t@";

	protected final String TEXT_160 = NL + "\tprotected int ";

	protected final String TEXT_161 = " = 0;" + NL;

	protected final String TEXT_162 = NL + "\t/**" + NL + "\t * The flag representing whether the ";

	protected final String TEXT_163 = " ";

	protected final String TEXT_164 = " has been set." + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */" + NL
			+ "\tprotected static final int ";

	protected final String TEXT_165 = "_ESETFLAG = 1 << ";

	protected final String TEXT_166 = ";" + NL;

	protected final String TEXT_167 = NL + "\t/**" + NL + "\t * This is true if the ";

	protected final String TEXT_168 = " ";

	protected final String TEXT_169 = " has been set." + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */";

	protected final String TEXT_170 = NL + "\t@";

	protected final String TEXT_171 = NL + "\tprotected boolean ";

	protected final String TEXT_172 = "ESet;" + NL;

	protected final String TEXT_173 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tprivate static final int ";

	protected final String TEXT_174 = " = ";

	protected final String TEXT_175 = ".getFeatureID(";

	protected final String TEXT_176 = ") - ";

	protected final String TEXT_177 = ";" + NL;

	protected final String TEXT_178 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tprivate static final int ";

	protected final String TEXT_179 = " = ";

	protected final String TEXT_180 = ".getFeatureID(";

	protected final String TEXT_181 = ") - ";

	protected final String TEXT_182 = ";" + NL;

	protected final String TEXT_183 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL
			+ "\tprivate static final int \"EOPERATION_OFFSET_CORRECTION\" = ";

	protected final String TEXT_184 = ".getOperationID(";

	protected final String TEXT_185 = ") - ";

	protected final String TEXT_186 = ";" + NL;

	protected final String TEXT_187 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\t";

	protected final String TEXT_188 = "public";

	protected final String TEXT_189 = "protected";

	protected final String TEXT_190 = " ";

	protected final String TEXT_191 = "()" + NL + "\t{" + NL + "\t\tsuper();";

	protected final String TEXT_192 = NL + "\t\t";

	protected final String TEXT_193 = " |= ";

	protected final String TEXT_194 = "_EFLAG";

	protected final String TEXT_195 = "_DEFAULT";

	protected final String TEXT_196 = ";";

	protected final String TEXT_197 = NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_198 = NL + "\t@Override";

	protected final String TEXT_199 = NL + "\tprotected ";

	protected final String TEXT_200 = " eStaticClass()" + NL + "\t{" + NL + "\t\treturn ";

	protected final String TEXT_201 = ";" + NL + "\t}" + NL;

	protected final String TEXT_202 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_203 = NL + "\t@Override";

	protected final String TEXT_204 = NL + "\tprotected int eStaticFeatureCount()" + NL + "\t{" + NL + "\t\treturn ";

	protected final String TEXT_205 = ";" + NL + "\t}" + NL;

	protected final String TEXT_206 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_207 = NL + "\t";

	protected final String TEXT_208 = "[] ";

	protected final String TEXT_209 = "();" + NL;

	protected final String TEXT_210 = NL + "\tpublic ";

	protected final String TEXT_211 = "[] ";

	protected final String TEXT_212 = "()" + NL + "\t{";

	protected final String TEXT_213 = NL + "\t\t";

	protected final String TEXT_214 = " list = (";

	protected final String TEXT_215 = ")";

	protected final String TEXT_216 = "();" + NL + "\t\tif (list.isEmpty()) return ";

	protected final String TEXT_217 = "(";

	protected final String TEXT_218 = "[])";

	protected final String TEXT_219 = "_EEMPTY_ARRAY;";

	protected final String TEXT_220 = NL + "\t\tif (";

	protected final String TEXT_221 = " == null || ";

	protected final String TEXT_222 = ".isEmpty()) return ";

	protected final String TEXT_223 = "(";

	protected final String TEXT_224 = "[])";

	protected final String TEXT_225 = "_EEMPTY_ARRAY;" + NL + "\t\t";

	protected final String TEXT_226 = " list = (";

	protected final String TEXT_227 = ")";

	protected final String TEXT_228 = ";";

	protected final String TEXT_229 = NL + "\t\tlist.shrink();" + NL + "\t\treturn (";

	protected final String TEXT_230 = "[])list.data();" + NL + "\t}" + NL;

	protected final String TEXT_231 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_232 = NL + "\t";

	protected final String TEXT_233 = " get";

	protected final String TEXT_234 = "(int index);" + NL;

	protected final String TEXT_235 = NL + "\tpublic ";

	protected final String TEXT_236 = " get";

	protected final String TEXT_237 = "(int index)" + NL + "\t{" + NL + "\t\treturn ";

	protected final String TEXT_238 = "(";

	protected final String TEXT_239 = ")";

	protected final String TEXT_240 = "().get(index);" + NL + "\t}" + NL;

	protected final String TEXT_241 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_242 = NL + "\tint get";

	protected final String TEXT_243 = "Length();" + NL;

	protected final String TEXT_244 = NL + "\tpublic int get";

	protected final String TEXT_245 = "Length()" + NL + "\t{";

	protected final String TEXT_246 = NL + "\t\treturn ";

	protected final String TEXT_247 = "().size();";

	protected final String TEXT_248 = NL + "\t\treturn ";

	protected final String TEXT_249 = " == null ? 0 : ";

	protected final String TEXT_250 = ".size();";

	protected final String TEXT_251 = NL + "\t}" + NL;

	protected final String TEXT_252 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_253 = NL + "\tvoid set";

	protected final String TEXT_254 = "(";

	protected final String TEXT_255 = "[] new";

	protected final String TEXT_256 = ");" + NL;

	protected final String TEXT_257 = NL + "\tpublic void set";

	protected final String TEXT_258 = "(";

	protected final String TEXT_259 = "[] new";

	protected final String TEXT_260 = ")" + NL + "\t{" + NL + "\t\t((";

	protected final String TEXT_261 = ")";

	protected final String TEXT_262 = "()).setData(new";

	protected final String TEXT_263 = ".length, new";

	protected final String TEXT_264 = ");" + NL + "\t}" + NL;

	protected final String TEXT_265 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_266 = NL + "\tvoid set";

	protected final String TEXT_267 = "(int index, ";

	protected final String TEXT_268 = " element);" + NL;

	protected final String TEXT_269 = NL + "\tpublic void set";

	protected final String TEXT_270 = "(int index, ";

	protected final String TEXT_271 = " element)" + NL + "\t{" + NL + "\t\t";

	protected final String TEXT_272 = "().set(index, element);" + NL + "\t}" + NL;

	protected final String TEXT_273 = NL + "\t/**" + NL + "\t * Returns the value of the '<em><b>";

	protected final String TEXT_274 = "</b></em>' ";

	protected final String TEXT_275 = ".";

	protected final String TEXT_276 = NL + "\t * The key is of type ";

	protected final String TEXT_277 = "list of {@link ";

	protected final String TEXT_278 = "}";

	protected final String TEXT_279 = "{@link ";

	protected final String TEXT_280 = "}";

	protected final String TEXT_281 = "," + NL + "\t * and the value is of type ";

	protected final String TEXT_282 = "list of {@link ";

	protected final String TEXT_283 = "}";

	protected final String TEXT_284 = "{@link ";

	protected final String TEXT_285 = "}";

	protected final String TEXT_286 = ",";

	protected final String TEXT_287 = NL + "\t * The list contents are of type {@link ";

	protected final String TEXT_288 = "}";

	protected final String TEXT_289 = ".";

	protected final String TEXT_290 = NL + "\t * The default value is <code>";

	protected final String TEXT_291 = "</code>.";

	protected final String TEXT_292 = NL + "\t * The literals are from the enumeration {@link ";

	protected final String TEXT_293 = "}.";

	protected final String TEXT_294 = NL + "\t * It is bidirectional and its opposite is '{@link ";

	protected final String TEXT_295 = "#";

	protected final String TEXT_296 = " <em>";

	protected final String TEXT_297 = "</em>}'.";

	protected final String TEXT_298 = NL + "\t * <!-- begin-user-doc -->";

	protected final String TEXT_299 = NL + "\t * <p>" + NL + "\t * If the meaning of the '<em>";

	protected final String TEXT_300 = "</em>' ";

	protected final String TEXT_301 = " isn't clear," + NL + "\t * there really should be more of a description here..."
			+ NL + "\t * </p>";

	protected final String TEXT_302 = NL + "\t * <!-- end-user-doc -->";

	protected final String TEXT_303 = NL + "\t * <!-- begin-model-doc -->" + NL + "\t * ";

	protected final String TEXT_304 = NL + "\t * <!-- end-model-doc -->";

	protected final String TEXT_305 = NL + "\t * @return the value of the '<em>";

	protected final String TEXT_306 = "</em>' ";

	protected final String TEXT_307 = ".";

	protected final String TEXT_308 = NL + "\t * @see ";

	protected final String TEXT_309 = NL + "\t * @see #isSet";

	protected final String TEXT_310 = "()";

	protected final String TEXT_311 = NL + "\t * @see #unset";

	protected final String TEXT_312 = "()";

	protected final String TEXT_313 = NL + "\t * @see #set";

	protected final String TEXT_314 = "(";

	protected final String TEXT_315 = ")";

	protected final String TEXT_316 = NL + "\t * @see ";

	protected final String TEXT_317 = "#get";

	protected final String TEXT_318 = "()";

	protected final String TEXT_319 = NL + "\t * @see ";

	protected final String TEXT_320 = "#";

	protected final String TEXT_321 = NL + "\t * @model ";

	protected final String TEXT_322 = NL + "\t *        ";

	protected final String TEXT_323 = NL + "\t * @model";

	protected final String TEXT_324 = NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_325 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_326 = NL + "\t";

	protected final String TEXT_327 = " ";

	protected final String TEXT_328 = "();" + NL;

	protected final String TEXT_329 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_330 = NL + "\tpublic ";

	protected final String TEXT_331 = " ";

	protected final String TEXT_332 = "_";

	protected final String TEXT_333 = "()" + NL + "\t{";

	protected final String TEXT_334 = NL + "\t\treturn ";

	protected final String TEXT_335 = "(";

	protected final String TEXT_336 = "(";

	protected final String TEXT_337 = ")eDynamicGet(";

	protected final String TEXT_338 = ", ";

	protected final String TEXT_339 = ", true, ";

	protected final String TEXT_340 = ")";

	protected final String TEXT_341 = ").";

	protected final String TEXT_342 = "()";

	protected final String TEXT_343 = ";";

	protected final String TEXT_344 = NL + "\t\treturn ";

	protected final String TEXT_345 = "(";

	protected final String TEXT_346 = "(";

	protected final String TEXT_347 = ")eGet(";

	protected final String TEXT_348 = ", true)";

	protected final String TEXT_349 = ").";

	protected final String TEXT_350 = "()";

	protected final String TEXT_351 = ";";

	protected final String TEXT_352 = NL + "\t\treturn ";

	protected final String TEXT_353 = "(";

	protected final String TEXT_354 = "(";

	protected final String TEXT_355 = ")";

	protected final String TEXT_356 = "__ESETTING_DELEGATE.dynamicGet(this, null, 0, true, false)";

	protected final String TEXT_357 = ").";

	protected final String TEXT_358 = "()";

	protected final String TEXT_359 = ";";

	protected final String TEXT_360 = NL + "\t\t";

	protected final String TEXT_361 = " ";

	protected final String TEXT_362 = " = (";

	protected final String TEXT_363 = ")eVirtualGet(";

	protected final String TEXT_364 = ");";

	protected final String TEXT_365 = NL + "\t\tif (";

	protected final String TEXT_366 = " == null)" + NL + "\t\t{";

	protected final String TEXT_367 = NL + "\t\t\teVirtualSet(";

	protected final String TEXT_368 = ", ";

	protected final String TEXT_369 = " = new ";

	protected final String TEXT_370 = ");";

	protected final String TEXT_371 = NL + "\t\t\t";

	protected final String TEXT_372 = " = new ";

	protected final String TEXT_373 = ";";

	protected final String TEXT_374 = NL + "\t\t}" + NL + "\t\treturn ";

	protected final String TEXT_375 = ";";

	protected final String TEXT_376 = NL + "\t\tif (eContainerFeatureID() != ";

	protected final String TEXT_377 = ") return null;" + NL + "\t\treturn (";

	protected final String TEXT_378 = ")eContainer();";

	protected final String TEXT_379 = NL + "\t\t";

	protected final String TEXT_380 = " ";

	protected final String TEXT_381 = " = (";

	protected final String TEXT_382 = ")eVirtualGet(";

	protected final String TEXT_383 = ", ";

	protected final String TEXT_384 = ");";

	protected final String TEXT_385 = NL + "\t\tif (";

	protected final String TEXT_386 = " != null && ";

	protected final String TEXT_387 = ".eIsProxy())" + NL + "\t\t{" + NL + "\t\t\t";

	protected final String TEXT_388 = " old";

	protected final String TEXT_389 = " = (";

	protected final String TEXT_390 = ")";

	protected final String TEXT_391 = ";" + NL + "\t\t\t";

	protected final String TEXT_392 = " = ";

	protected final String TEXT_393 = "eResolveProxy(old";

	protected final String TEXT_394 = ");" + NL + "\t\t\tif (";

	protected final String TEXT_395 = " != old";

	protected final String TEXT_396 = ")" + NL + "\t\t\t{";

	protected final String TEXT_397 = NL + "\t\t\t\t";

	protected final String TEXT_398 = " new";

	protected final String TEXT_399 = " = (";

	protected final String TEXT_400 = ")";

	protected final String TEXT_401 = ";";

	protected final String TEXT_402 = NL + "\t\t\t\t";

	protected final String TEXT_403 = " msgs = old";

	protected final String TEXT_404 = ".eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ";

	protected final String TEXT_405 = ", null, null);";

	protected final String TEXT_406 = NL + "\t\t\t\t";

	protected final String TEXT_407 = " msgs =  old";

	protected final String TEXT_408 = ".eInverseRemove(this, ";

	protected final String TEXT_409 = ", ";

	protected final String TEXT_410 = ".class, null);";

	protected final String TEXT_411 = NL + "\t\t\t\tif (new";

	protected final String TEXT_412 = ".eInternalContainer() == null)" + NL + "\t\t\t\t{";

	protected final String TEXT_413 = NL + "\t\t\t\t\tmsgs = new";

	protected final String TEXT_414 = ".eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ";

	protected final String TEXT_415 = ", null, msgs);";

	protected final String TEXT_416 = NL + "\t\t\t\t\tmsgs =  new";

	protected final String TEXT_417 = ".eInverseAdd(this, ";

	protected final String TEXT_418 = ", ";

	protected final String TEXT_419 = ".class, msgs);";

	protected final String TEXT_420 = NL + "\t\t\t\t}" + NL + "\t\t\t\tif (msgs != null) msgs.dispatch();";

	protected final String TEXT_421 = NL + "\t\t\t\teVirtualSet(";

	protected final String TEXT_422 = ", ";

	protected final String TEXT_423 = ");";

	protected final String TEXT_424 = NL + "\t\t\t\tif (eNotificationRequired())" + NL + "\t\t\t\t\teNotify(new ";

	protected final String TEXT_425 = "(this, ";

	protected final String TEXT_426 = ".RESOLVE, ";

	protected final String TEXT_427 = ", old";

	protected final String TEXT_428 = ", ";

	protected final String TEXT_429 = "));";

	protected final String TEXT_430 = NL + "\t\t\t}" + NL + "\t\t}";

	protected final String TEXT_431 = NL + "\t\treturn (";

	protected final String TEXT_432 = ")eVirtualGet(";

	protected final String TEXT_433 = ", ";

	protected final String TEXT_434 = ");";

	protected final String TEXT_435 = NL + "\t\treturn (";

	protected final String TEXT_436 = " & ";

	protected final String TEXT_437 = "_EFLAG) != 0;";

	protected final String TEXT_438 = NL + "\t\treturn ";

	protected final String TEXT_439 = "_EFLAG_VALUES[(";

	protected final String TEXT_440 = " & ";

	protected final String TEXT_441 = "_EFLAG) >>> ";

	protected final String TEXT_442 = "_EFLAG_OFFSET];";

	protected final String TEXT_443 = NL + "\t\treturn ";

	protected final String TEXT_444 = ";";

	protected final String TEXT_445 = NL + "\t\t";

	protected final String TEXT_446 = " ";

	protected final String TEXT_447 = " = basicGet";

	protected final String TEXT_448 = "();" + NL + "\t\treturn ";

	protected final String TEXT_449 = " != null && ";

	protected final String TEXT_450 = ".eIsProxy() ? ";

	protected final String TEXT_451 = "eResolveProxy((";

	protected final String TEXT_452 = ")";

	protected final String TEXT_453 = ") : ";

	protected final String TEXT_454 = ";";

	protected final String TEXT_455 = NL + "\t\treturn new ";

	protected final String TEXT_456 = "((";

	protected final String TEXT_457 = ".Internal)((";

	protected final String TEXT_458 = ".Internal.Wrapper)get";

	protected final String TEXT_459 = "()).featureMap().";

	protected final String TEXT_460 = "list(";

	protected final String TEXT_461 = "));";

	protected final String TEXT_462 = NL + "\t\treturn (";

	protected final String TEXT_463 = ")get";

	protected final String TEXT_464 = "().";

	protected final String TEXT_465 = "list(";

	protected final String TEXT_466 = ");";

	protected final String TEXT_467 = NL + "\t\treturn ((";

	protected final String TEXT_468 = ".Internal.Wrapper)get";

	protected final String TEXT_469 = "()).featureMap().list(";

	protected final String TEXT_470 = ");";

	protected final String TEXT_471 = NL + "\t\treturn get";

	protected final String TEXT_472 = "().list(";

	protected final String TEXT_473 = ");";

	protected final String TEXT_474 = NL + "\t\treturn ";

	protected final String TEXT_475 = "(";

	protected final String TEXT_476 = "(";

	protected final String TEXT_477 = ")";

	protected final String TEXT_478 = "((";

	protected final String TEXT_479 = ".Internal.Wrapper)get";

	protected final String TEXT_480 = "()).featureMap().get(";

	protected final String TEXT_481 = ", true)";

	protected final String TEXT_482 = ").";

	protected final String TEXT_483 = "()";

	protected final String TEXT_484 = ";";

	protected final String TEXT_485 = NL + "\t\treturn ";

	protected final String TEXT_486 = "(";

	protected final String TEXT_487 = "(";

	protected final String TEXT_488 = ")";

	protected final String TEXT_489 = "get";

	protected final String TEXT_490 = "().get(";

	protected final String TEXT_491 = ", true)";

	protected final String TEXT_492 = ").";

	protected final String TEXT_493 = "()";

	protected final String TEXT_494 = ";";

	protected final String TEXT_495 = NL + "\t\t";

	protected final String TEXT_496 = NL + "\t\t";

	protected final String TEXT_497 = NL + "\t\t// TODO: implement this method to return the '";

	protected final String TEXT_498 = "' ";

	protected final String TEXT_499 = NL + "\t\t// Ensure that you remove @generated or mark it @generated NOT";

	protected final String TEXT_500 = NL
			+ "\t\t// The list is expected to implement org.eclipse.emf.ecore.util.InternalEList and org.eclipse.emf.ecore.EStructuralFeature.Setting"
			+ NL + "\t\t// so it's likely that an appropriate subclass of org.eclipse.emf.ecore.util.";

	protected final String TEXT_501 = "EcoreEMap";

	protected final String TEXT_502 = "BasicFeatureMap";

	protected final String TEXT_503 = "EcoreEList";

	protected final String TEXT_504 = " should be used.";

	protected final String TEXT_505 = NL + "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_506 = NL + "\t}" + NL;

	protected final String TEXT_507 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_508 = NL + "\tpublic ";

	protected final String TEXT_509 = " basicGet";

	protected final String TEXT_510 = "()" + NL + "\t{";

	protected final String TEXT_511 = NL + "\t\treturn (";

	protected final String TEXT_512 = ")eDynamicGet(";

	protected final String TEXT_513 = ", ";

	protected final String TEXT_514 = ", false, ";

	protected final String TEXT_515 = ");";

	protected final String TEXT_516 = NL + "\t\treturn ";

	protected final String TEXT_517 = "(";

	protected final String TEXT_518 = "(";

	protected final String TEXT_519 = ")";

	protected final String TEXT_520 = "__ESETTING_DELEGATE.dynamicGet(this, null, 0, false, false)";

	protected final String TEXT_521 = ").";

	protected final String TEXT_522 = "()";

	protected final String TEXT_523 = ";";

	protected final String TEXT_524 = NL + "\t\tif (eContainerFeatureID() != ";

	protected final String TEXT_525 = ") return null;" + NL + "\t\treturn (";

	protected final String TEXT_526 = ")eInternalContainer();";

	protected final String TEXT_527 = NL + "\t\treturn (";

	protected final String TEXT_528 = ")eVirtualGet(";

	protected final String TEXT_529 = ");";

	protected final String TEXT_530 = NL + "\t\treturn ";

	protected final String TEXT_531 = ";";

	protected final String TEXT_532 = NL + "\t\treturn (";

	protected final String TEXT_533 = ")((";

	protected final String TEXT_534 = ".Internal.Wrapper)get";

	protected final String TEXT_535 = "()).featureMap().get(";

	protected final String TEXT_536 = ", false);";

	protected final String TEXT_537 = NL + "\t\treturn (";

	protected final String TEXT_538 = ")get";

	protected final String TEXT_539 = "().get(";

	protected final String TEXT_540 = ", false);";

	protected final String TEXT_541 = NL + "\t\t// TODO: implement this method to return the '";

	protected final String TEXT_542 = "' ";

	protected final String TEXT_543 = NL + "\t\t// -> do not perform proxy resolution" + NL
			+ "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL
			+ "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_544 = NL + "\t}" + NL;

	protected final String TEXT_545 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_546 = NL + "\tpublic ";

	protected final String TEXT_547 = " basicSet";

	protected final String TEXT_548 = "(";

	protected final String TEXT_549 = " new";

	protected final String TEXT_550 = ", ";

	protected final String TEXT_551 = " msgs)" + NL + "\t{";

	protected final String TEXT_552 = NL + "\t\tmsgs = eBasicSetContainer((";

	protected final String TEXT_553 = ")new";

	protected final String TEXT_554 = ", ";

	protected final String TEXT_555 = ", msgs);";

	protected final String TEXT_556 = NL + "\t\treturn msgs;";

	protected final String TEXT_557 = NL + "\t\tmsgs = eDynamicInverseAdd((";

	protected final String TEXT_558 = ")new";

	protected final String TEXT_559 = ", ";

	protected final String TEXT_560 = ", msgs);";

	protected final String TEXT_561 = NL + "\t\treturn msgs;";

	protected final String TEXT_562 = NL + "\t\tObject old";

	protected final String TEXT_563 = " = eVirtualSet(";

	protected final String TEXT_564 = ", new";

	protected final String TEXT_565 = ");";

	protected final String TEXT_566 = NL + "\t\t";

	protected final String TEXT_567 = " old";

	protected final String TEXT_568 = " = ";

	protected final String TEXT_569 = ";" + NL + "\t\t";

	protected final String TEXT_570 = " = new";

	protected final String TEXT_571 = ";";

	protected final String TEXT_572 = NL + "\t\tboolean isSetChange = old";

	protected final String TEXT_573 = " == EVIRTUAL_NO_VALUE;";

	protected final String TEXT_574 = NL + "\t\tboolean old";

	protected final String TEXT_575 = "ESet = (";

	protected final String TEXT_576 = " & ";

	protected final String TEXT_577 = "_ESETFLAG) != 0;";

	protected final String TEXT_578 = NL + "\t\t";

	protected final String TEXT_579 = " |= ";

	protected final String TEXT_580 = "_ESETFLAG;";

	protected final String TEXT_581 = NL + "\t\tboolean old";

	protected final String TEXT_582 = "ESet = ";

	protected final String TEXT_583 = "ESet;";

	protected final String TEXT_584 = NL + "\t\t";

	protected final String TEXT_585 = "ESet = true;";

	protected final String TEXT_586 = NL + "\t\tif (eNotificationRequired())" + NL + "\t\t{";

	protected final String TEXT_587 = NL + "\t\t\t";

	protected final String TEXT_588 = " notification = new ";

	protected final String TEXT_589 = "(this, ";

	protected final String TEXT_590 = ".SET, ";

	protected final String TEXT_591 = ", ";

	protected final String TEXT_592 = "isSetChange ? null : old";

	protected final String TEXT_593 = "old";

	protected final String TEXT_594 = ", new";

	protected final String TEXT_595 = ", ";

	protected final String TEXT_596 = "isSetChange";

	protected final String TEXT_597 = "!old";

	protected final String TEXT_598 = "ESet";

	protected final String TEXT_599 = ");";

	protected final String TEXT_600 = NL + "\t\t\t";

	protected final String TEXT_601 = " notification = new ";

	protected final String TEXT_602 = "(this, ";

	protected final String TEXT_603 = ".SET, ";

	protected final String TEXT_604 = ", ";

	protected final String TEXT_605 = "old";

	protected final String TEXT_606 = " == EVIRTUAL_NO_VALUE ? null : old";

	protected final String TEXT_607 = "old";

	protected final String TEXT_608 = ", new";

	protected final String TEXT_609 = ");";

	protected final String TEXT_610 = NL + "\t\t\tif (msgs == null) msgs = notification; else msgs.add(notification);"
			+ NL + "\t\t}";

	protected final String TEXT_611 = NL + "\t\treturn msgs;";

	protected final String TEXT_612 = NL + "\t\treturn ((";

	protected final String TEXT_613 = ".Internal)((";

	protected final String TEXT_614 = ".Internal.Wrapper)get";

	protected final String TEXT_615 = "()).featureMap()).basicAdd(";

	protected final String TEXT_616 = ", new";

	protected final String TEXT_617 = ", msgs);";

	protected final String TEXT_618 = NL + "\t\treturn ((";

	protected final String TEXT_619 = ".Internal)get";

	protected final String TEXT_620 = "()).basicAdd(";

	protected final String TEXT_621 = ", new";

	protected final String TEXT_622 = ", msgs);";

	protected final String TEXT_623 = NL + "\t\t// TODO: implement this method to set the contained '";

	protected final String TEXT_624 = "' ";

	protected final String TEXT_625 = NL
			+ "\t\t// -> this method is automatically invoked to keep the containment relationship in synch" + NL
			+ "\t\t// -> do not modify other features" + NL
			+ "\t\t// -> return msgs, after adding any generated Notification to it (if it is null, a NotificationChain object must be created first)"
			+ NL + "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL
			+ "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_626 = NL + "\t}" + NL;

	protected final String TEXT_627 = NL + "\t/**" + NL + "\t * Sets the value of the '{@link ";

	protected final String TEXT_628 = "#";

	protected final String TEXT_629 = " <em>";

	protected final String TEXT_630 = "</em>}' ";

	protected final String TEXT_631 = ".";

	protected final String TEXT_632 = NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @param value the new value of the '<em>";

	protected final String TEXT_633 = "</em>' ";

	protected final String TEXT_634 = ".";

	protected final String TEXT_635 = NL + "\t * @see ";

	protected final String TEXT_636 = NL + "\t * @see #isSet";

	protected final String TEXT_637 = "()";

	protected final String TEXT_638 = NL + "\t * @see #unset";

	protected final String TEXT_639 = "()";

	protected final String TEXT_640 = NL + "\t * @see #";

	protected final String TEXT_641 = "()" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_642 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_643 = NL + "\tvoid set";

	protected final String TEXT_644 = "(";

	protected final String TEXT_645 = " value);" + NL;

	protected final String TEXT_646 = NL + "\tpublic void set";

	protected final String TEXT_647 = "_";

	protected final String TEXT_648 = "(";

	protected final String TEXT_649 = " ";

	protected final String TEXT_650 = ")" + NL + "\t{";

	protected final String TEXT_651 = NL + "\t\teDynamicSet(";

	protected final String TEXT_652 = ", ";

	protected final String TEXT_653 = ", ";

	protected final String TEXT_654 = "new ";

	protected final String TEXT_655 = "(";

	protected final String TEXT_656 = "new";

	protected final String TEXT_657 = ")";

	protected final String TEXT_658 = ");";

	protected final String TEXT_659 = NL + "\t\teSet(";

	protected final String TEXT_660 = ", ";

	protected final String TEXT_661 = "new ";

	protected final String TEXT_662 = "(";

	protected final String TEXT_663 = "new";

	protected final String TEXT_664 = ")";

	protected final String TEXT_665 = ");";

	protected final String TEXT_666 = NL + "\t\t";

	protected final String TEXT_667 = "__ESETTING_DELEGATE.dynamicSet(this, null, 0, ";

	protected final String TEXT_668 = "new ";

	protected final String TEXT_669 = "(";

	protected final String TEXT_670 = "new";

	protected final String TEXT_671 = ")";

	protected final String TEXT_672 = ");";

	protected final String TEXT_673 = NL + "\t\tif (new";

	protected final String TEXT_674 = " != eInternalContainer() || (eContainerFeatureID() != ";

	protected final String TEXT_675 = " && new";

	protected final String TEXT_676 = " != null))" + NL + "\t\t{" + NL + "\t\t\tif (";

	protected final String TEXT_677 = ".isAncestor(this, ";

	protected final String TEXT_678 = "new";

	protected final String TEXT_679 = "))" + NL + "\t\t\t\tthrow new ";

	protected final String TEXT_680 = "(\"Recursive containment not allowed for \" + toString());";

	protected final String TEXT_681 = NL + "\t\t\t";

	protected final String TEXT_682 = " msgs = null;" + NL + "\t\t\tif (eInternalContainer() != null)" + NL
			+ "\t\t\t\tmsgs = eBasicRemoveFromContainer(msgs);" + NL + "\t\t\tif (new";

	protected final String TEXT_683 = " != null)" + NL + "\t\t\t\tmsgs = ((";

	protected final String TEXT_684 = ")new";

	protected final String TEXT_685 = ").eInverseAdd(this, ";

	protected final String TEXT_686 = ", ";

	protected final String TEXT_687 = ".class, msgs);" + NL + "\t\t\tmsgs = basicSet";

	protected final String TEXT_688 = "(";

	protected final String TEXT_689 = "new";

	protected final String TEXT_690 = ", msgs);" + NL + "\t\t\tif (msgs != null) msgs.dispatch();" + NL + "\t\t}";

	protected final String TEXT_691 = NL + "\t\telse if (eNotificationRequired())" + NL + "\t\t\teNotify(new ";

	protected final String TEXT_692 = "(this, ";

	protected final String TEXT_693 = ".SET, ";

	protected final String TEXT_694 = ", new";

	protected final String TEXT_695 = ", new";

	protected final String TEXT_696 = "));";

	protected final String TEXT_697 = NL + "\t\t";

	protected final String TEXT_698 = " ";

	protected final String TEXT_699 = " = (";

	protected final String TEXT_700 = ")eVirtualGet(";

	protected final String TEXT_701 = ");";

	protected final String TEXT_702 = NL + "\t\tif (new";

	protected final String TEXT_703 = " != ";

	protected final String TEXT_704 = ")" + NL + "\t\t{" + NL + "\t\t\t";

	protected final String TEXT_705 = " msgs = null;" + NL + "\t\t\tif (";

	protected final String TEXT_706 = " != null)";

	protected final String TEXT_707 = NL + "\t\t\t\tmsgs = ((";

	protected final String TEXT_708 = ")";

	protected final String TEXT_709 = ").eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ";

	protected final String TEXT_710 = ", null, msgs);" + NL + "\t\t\tif (new";

	protected final String TEXT_711 = " != null)" + NL + "\t\t\t\tmsgs = ((";

	protected final String TEXT_712 = ")new";

	protected final String TEXT_713 = ").eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ";

	protected final String TEXT_714 = ", null, msgs);";

	protected final String TEXT_715 = NL + "\t\t\t\tmsgs = ((";

	protected final String TEXT_716 = ")";

	protected final String TEXT_717 = ").eInverseRemove(this, ";

	protected final String TEXT_718 = ", ";

	protected final String TEXT_719 = ".class, msgs);" + NL + "\t\t\tif (new";

	protected final String TEXT_720 = " != null)" + NL + "\t\t\t\tmsgs = ((";

	protected final String TEXT_721 = ")new";

	protected final String TEXT_722 = ").eInverseAdd(this, ";

	protected final String TEXT_723 = ", ";

	protected final String TEXT_724 = ".class, msgs);";

	protected final String TEXT_725 = NL + "\t\t\tmsgs = basicSet";

	protected final String TEXT_726 = "(";

	protected final String TEXT_727 = "new";

	protected final String TEXT_728 = ", msgs);" + NL + "\t\t\tif (msgs != null) msgs.dispatch();" + NL + "\t\t}";

	protected final String TEXT_729 = NL + "\t\telse" + NL + "\t\t{";

	protected final String TEXT_730 = NL + "\t\t\tboolean old";

	protected final String TEXT_731 = "ESet = eVirtualIsSet(";

	protected final String TEXT_732 = ");";

	protected final String TEXT_733 = NL + "\t\t\tboolean old";

	protected final String TEXT_734 = "ESet = (";

	protected final String TEXT_735 = " & ";

	protected final String TEXT_736 = "_ESETFLAG) != 0;";

	protected final String TEXT_737 = NL + "\t\t\t";

	protected final String TEXT_738 = " |= ";

	protected final String TEXT_739 = "_ESETFLAG;";

	protected final String TEXT_740 = NL + "\t\t\tboolean old";

	protected final String TEXT_741 = "ESet = ";

	protected final String TEXT_742 = "ESet;";

	protected final String TEXT_743 = NL + "\t\t\t";

	protected final String TEXT_744 = "ESet = true;";

	protected final String TEXT_745 = NL + "\t\t\tif (eNotificationRequired())" + NL + "\t\t\t\teNotify(new ";

	protected final String TEXT_746 = "(this, ";

	protected final String TEXT_747 = ".SET, ";

	protected final String TEXT_748 = ", new";

	protected final String TEXT_749 = ", new";

	protected final String TEXT_750 = ", !old";

	protected final String TEXT_751 = "ESet));";

	protected final String TEXT_752 = NL + "\t\t}";

	protected final String TEXT_753 = NL + "\t\telse if (eNotificationRequired())" + NL + "\t\t\teNotify(new ";

	protected final String TEXT_754 = "(this, ";

	protected final String TEXT_755 = ".SET, ";

	protected final String TEXT_756 = ", new";

	protected final String TEXT_757 = ", new";

	protected final String TEXT_758 = "));";

	protected final String TEXT_759 = NL + "\t\t";

	protected final String TEXT_760 = " old";

	protected final String TEXT_761 = " = (";

	protected final String TEXT_762 = " & ";

	protected final String TEXT_763 = "_EFLAG) != 0;";

	protected final String TEXT_764 = NL + "\t\t";

	protected final String TEXT_765 = " old";

	protected final String TEXT_766 = " = ";

	protected final String TEXT_767 = "_EFLAG_VALUES[(";

	protected final String TEXT_768 = " & ";

	protected final String TEXT_769 = "_EFLAG) >>> ";

	protected final String TEXT_770 = "_EFLAG_OFFSET];";

	protected final String TEXT_771 = NL + "\t\tif (new";

	protected final String TEXT_772 = ") ";

	protected final String TEXT_773 = " |= ";

	protected final String TEXT_774 = "_EFLAG; else ";

	protected final String TEXT_775 = " &= ~";

	protected final String TEXT_776 = "_EFLAG;";

	protected final String TEXT_777 = NL + "\t\tif (new";

	protected final String TEXT_778 = " == null) new";

	protected final String TEXT_779 = " = ";

	protected final String TEXT_780 = "_EDEFAULT;" + NL + "\t\t";

	protected final String TEXT_781 = " = ";

	protected final String TEXT_782 = " & ~";

	protected final String TEXT_783 = "_EFLAG | ";

	protected final String TEXT_784 = "new";

	protected final String TEXT_785 = ".ordinal()";

	protected final String TEXT_786 = ".VALUES.indexOf(new";

	protected final String TEXT_787 = ")";

	protected final String TEXT_788 = " << ";

	protected final String TEXT_789 = "_EFLAG_OFFSET;";

	protected final String TEXT_790 = NL + "\t\t";

	protected final String TEXT_791 = " old";

	protected final String TEXT_792 = " = ";

	protected final String TEXT_793 = ";";

	protected final String TEXT_794 = NL + "\t\t";

	protected final String TEXT_795 = " ";

	protected final String TEXT_796 = " = new";

	protected final String TEXT_797 = " == null ? ";

	protected final String TEXT_798 = " : new";

	protected final String TEXT_799 = ";";

	protected final String TEXT_800 = NL + "\t\t";

	protected final String TEXT_801 = " = new";

	protected final String TEXT_802 = " == null ? ";

	protected final String TEXT_803 = " : new";

	protected final String TEXT_804 = ";";

	protected final String TEXT_805 = NL + "\t\t";

	protected final String TEXT_806 = " ";

	protected final String TEXT_807 = " = ";

	protected final String TEXT_808 = "new";

	protected final String TEXT_809 = ";";

	protected final String TEXT_810 = NL + "\t\t";

	protected final String TEXT_811 = " = ";

	protected final String TEXT_812 = "new";

	protected final String TEXT_813 = ";";

	protected final String TEXT_814 = NL + "\t\tObject old";

	protected final String TEXT_815 = " = eVirtualSet(";

	protected final String TEXT_816 = ", ";

	protected final String TEXT_817 = ");";

	protected final String TEXT_818 = NL + "\t\tboolean isSetChange = old";

	protected final String TEXT_819 = " == EVIRTUAL_NO_VALUE;";

	protected final String TEXT_820 = NL + "\t\tboolean old";

	protected final String TEXT_821 = "ESet = (";

	protected final String TEXT_822 = " & ";

	protected final String TEXT_823 = "_ESETFLAG) != 0;";

	protected final String TEXT_824 = NL + "\t\t";

	protected final String TEXT_825 = " |= ";

	protected final String TEXT_826 = "_ESETFLAG;";

	protected final String TEXT_827 = NL + "\t\tboolean old";

	protected final String TEXT_828 = "ESet = ";

	protected final String TEXT_829 = "ESet;";

	protected final String TEXT_830 = NL + "\t\t";

	protected final String TEXT_831 = "ESet = true;";

	protected final String TEXT_832 = NL + "\t\tif (eNotificationRequired())" + NL + "\t\t\teNotify(new ";

	protected final String TEXT_833 = "(this, ";

	protected final String TEXT_834 = ".SET, ";

	protected final String TEXT_835 = ", ";

	protected final String TEXT_836 = "isSetChange ? ";

	protected final String TEXT_837 = " : old";

	protected final String TEXT_838 = "old";

	protected final String TEXT_839 = ", ";

	protected final String TEXT_840 = "new";

	protected final String TEXT_841 = ", ";

	protected final String TEXT_842 = "isSetChange";

	protected final String TEXT_843 = "!old";

	protected final String TEXT_844 = "ESet";

	protected final String TEXT_845 = "));";

	protected final String TEXT_846 = NL + "\t\tif (eNotificationRequired())" + NL + "\t\t\teNotify(new ";

	protected final String TEXT_847 = "(this, ";

	protected final String TEXT_848 = ".SET, ";

	protected final String TEXT_849 = ", ";

	protected final String TEXT_850 = "old";

	protected final String TEXT_851 = " == EVIRTUAL_NO_VALUE ? ";

	protected final String TEXT_852 = " : old";

	protected final String TEXT_853 = "old";

	protected final String TEXT_854 = ", ";

	protected final String TEXT_855 = "new";

	protected final String TEXT_856 = "));";

	protected final String TEXT_857 = NL + "\t\t((";

	protected final String TEXT_858 = ".Internal)((";

	protected final String TEXT_859 = ".Internal.Wrapper)get";

	protected final String TEXT_860 = "()).featureMap()).set(";

	protected final String TEXT_861 = ", ";

	protected final String TEXT_862 = "new ";

	protected final String TEXT_863 = "(";

	protected final String TEXT_864 = "new";

	protected final String TEXT_865 = ")";

	protected final String TEXT_866 = ");";

	protected final String TEXT_867 = NL + "\t\t((";

	protected final String TEXT_868 = ".Internal)get";

	protected final String TEXT_869 = "()).set(";

	protected final String TEXT_870 = ", ";

	protected final String TEXT_871 = "new ";

	protected final String TEXT_872 = "(";

	protected final String TEXT_873 = "new";

	protected final String TEXT_874 = ")";

	protected final String TEXT_875 = ");";

	protected final String TEXT_876 = NL + "\t\t";

	protected final String TEXT_877 = NL + "\t\t// TODO: implement this method to set the '";

	protected final String TEXT_878 = "' ";

	protected final String TEXT_879 = NL + "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL
			+ "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_880 = NL + "\t}" + NL;

	protected final String TEXT_881 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_882 = NL + "\tpublic ";

	protected final String TEXT_883 = " basicUnset";

	protected final String TEXT_884 = "(";

	protected final String TEXT_885 = " msgs)" + NL + "\t{";

	protected final String TEXT_886 = NL + "\t\treturn eDynamicInverseRemove((";

	protected final String TEXT_887 = ")";

	protected final String TEXT_888 = "basicGet";

	protected final String TEXT_889 = "(), ";

	protected final String TEXT_890 = ", msgs);";

	protected final String TEXT_891 = "Object old";

	protected final String TEXT_892 = " = ";

	protected final String TEXT_893 = "eVirtualUnset(";

	protected final String TEXT_894 = ");";

	protected final String TEXT_895 = NL + "\t\t";

	protected final String TEXT_896 = " old";

	protected final String TEXT_897 = " = ";

	protected final String TEXT_898 = ";";

	protected final String TEXT_899 = NL + "\t\t";

	protected final String TEXT_900 = " = null;";

	protected final String TEXT_901 = NL + "\t\tboolean isSetChange = old";

	protected final String TEXT_902 = " != EVIRTUAL_NO_VALUE;";

	protected final String TEXT_903 = NL + "\t\tboolean old";

	protected final String TEXT_904 = "ESet = (";

	protected final String TEXT_905 = " & ";

	protected final String TEXT_906 = "_ESETFLAG) != 0;";

	protected final String TEXT_907 = NL + "\t\t";

	protected final String TEXT_908 = " &= ~";

	protected final String TEXT_909 = "_ESETFLAG;";

	protected final String TEXT_910 = NL + "\t\tboolean old";

	protected final String TEXT_911 = "ESet = ";

	protected final String TEXT_912 = "ESet;";

	protected final String TEXT_913 = NL + "\t\t";

	protected final String TEXT_914 = "ESet = false;";

	protected final String TEXT_915 = NL + "\t\tif (eNotificationRequired())" + NL + "\t\t{" + NL + "\t\t\t";

	protected final String TEXT_916 = " notification = new ";

	protected final String TEXT_917 = "(this, ";

	protected final String TEXT_918 = ".UNSET, ";

	protected final String TEXT_919 = ", ";

	protected final String TEXT_920 = "isSetChange ? old";

	protected final String TEXT_921 = " : null";

	protected final String TEXT_922 = "old";

	protected final String TEXT_923 = ", null, ";

	protected final String TEXT_924 = "isSetChange";

	protected final String TEXT_925 = "old";

	protected final String TEXT_926 = "ESet";

	protected final String TEXT_927 = ");" + NL
			+ "\t\t\tif (msgs == null) msgs = notification; else msgs.add(notification);" + NL + "\t\t}" + NL
			+ "\t\treturn msgs;";

	protected final String TEXT_928 = NL + "\t\t// TODO: implement this method to unset the contained '";

	protected final String TEXT_929 = "' ";

	protected final String TEXT_930 = NL
			+ "\t\t// -> this method is automatically invoked to keep the containment relationship in synch" + NL
			+ "\t\t// -> do not modify other features" + NL
			+ "\t\t// -> return msgs, after adding any generated Notification to it (if it is null, a NotificationChain object must be created first)"
			+ NL + "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL
			+ "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_931 = NL + "\t}" + NL;

	protected final String TEXT_932 = NL + "\t/**" + NL + "\t * Unsets the value of the '{@link ";

	protected final String TEXT_933 = "#";

	protected final String TEXT_934 = " <em>";

	protected final String TEXT_935 = "</em>}' ";

	protected final String TEXT_936 = ".";

	protected final String TEXT_937 = NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->";

	protected final String TEXT_938 = NL + "\t * @see #isSet";

	protected final String TEXT_939 = "()";

	protected final String TEXT_940 = NL + "\t * @see #";

	protected final String TEXT_941 = "()";

	protected final String TEXT_942 = NL + "\t * @see #set";

	protected final String TEXT_943 = "(";

	protected final String TEXT_944 = ")";

	protected final String TEXT_945 = NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_946 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_947 = NL + "\tvoid unset";

	protected final String TEXT_948 = "();" + NL;

	protected final String TEXT_949 = NL + "\tpublic void unset";

	protected final String TEXT_950 = "_";

	protected final String TEXT_951 = "()" + NL + "\t{";

	protected final String TEXT_952 = NL + "\t\teDynamicUnset(";

	protected final String TEXT_953 = ", ";

	protected final String TEXT_954 = ");";

	protected final String TEXT_955 = NL + "\t\teUnset(";

	protected final String TEXT_956 = ");";

	protected final String TEXT_957 = NL + "\t\t";

	protected final String TEXT_958 = "__ESETTING_DELEGATE.dynamicUnset(this, null, 0);";

	protected final String TEXT_959 = NL + "\t\t";

	protected final String TEXT_960 = " ";

	protected final String TEXT_961 = " = (";

	protected final String TEXT_962 = ")eVirtualGet(";

	protected final String TEXT_963 = ");";

	protected final String TEXT_964 = NL + "\t\tif (";

	protected final String TEXT_965 = " != null) ((";

	protected final String TEXT_966 = ".Unsettable";

	protected final String TEXT_967 = ")";

	protected final String TEXT_968 = ").unset();";

	protected final String TEXT_969 = NL + "\t\t";

	protected final String TEXT_970 = " ";

	protected final String TEXT_971 = " = (";

	protected final String TEXT_972 = ")eVirtualGet(";

	protected final String TEXT_973 = ");";

	protected final String TEXT_974 = NL + "\t\tif (";

	protected final String TEXT_975 = " != null)" + NL + "\t\t{" + NL + "\t\t\t";

	protected final String TEXT_976 = " msgs = null;";

	protected final String TEXT_977 = NL + "\t\t\tmsgs = ((";

	protected final String TEXT_978 = ")";

	protected final String TEXT_979 = ").eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ";

	protected final String TEXT_980 = ", null, msgs);";

	protected final String TEXT_981 = NL + "\t\t\tmsgs = ((";

	protected final String TEXT_982 = ")";

	protected final String TEXT_983 = ").eInverseRemove(this, ";

	protected final String TEXT_984 = ", ";

	protected final String TEXT_985 = ".class, msgs);";

	protected final String TEXT_986 = NL + "\t\t\tmsgs = basicUnset";

	protected final String TEXT_987 = "(msgs);" + NL + "\t\t\tif (msgs != null) msgs.dispatch();" + NL + "\t\t}" + NL
			+ "\t\telse" + NL + "\t\t{";

	protected final String TEXT_988 = NL + "\t\t\tboolean old";

	protected final String TEXT_989 = "ESet = eVirtualIsSet(";

	protected final String TEXT_990 = ");";

	protected final String TEXT_991 = NL + "\t\t\tboolean old";

	protected final String TEXT_992 = "ESet = (";

	protected final String TEXT_993 = " & ";

	protected final String TEXT_994 = "_ESETFLAG) != 0;";

	protected final String TEXT_995 = NL + "\t\t\t";

	protected final String TEXT_996 = " &= ~";

	protected final String TEXT_997 = "_ESETFLAG;";

	protected final String TEXT_998 = NL + "\t\t\tboolean old";

	protected final String TEXT_999 = "ESet = ";

	protected final String TEXT_1000 = "ESet;";

	protected final String TEXT_1001 = NL + "\t\t\t";

	protected final String TEXT_1002 = "ESet = false;";

	protected final String TEXT_1003 = NL + "\t\t\tif (eNotificationRequired())" + NL + "\t\t\t\teNotify(new ";

	protected final String TEXT_1004 = "(this, ";

	protected final String TEXT_1005 = ".UNSET, ";

	protected final String TEXT_1006 = ", null, null, old";

	protected final String TEXT_1007 = "ESet));";

	protected final String TEXT_1008 = NL + "\t\t}";

	protected final String TEXT_1009 = NL + "\t\t";

	protected final String TEXT_1010 = " old";

	protected final String TEXT_1011 = " = (";

	protected final String TEXT_1012 = " & ";

	protected final String TEXT_1013 = "_EFLAG) != 0;";

	protected final String TEXT_1014 = NL + "\t\t";

	protected final String TEXT_1015 = " old";

	protected final String TEXT_1016 = " = ";

	protected final String TEXT_1017 = "_EFLAG_VALUES[(";

	protected final String TEXT_1018 = " & ";

	protected final String TEXT_1019 = "_EFLAG) >>> ";

	protected final String TEXT_1020 = "_EFLAG_OFFSET];";

	protected final String TEXT_1021 = NL + "\t\tObject old";

	protected final String TEXT_1022 = " = eVirtualUnset(";

	protected final String TEXT_1023 = ");";

	protected final String TEXT_1024 = NL + "\t\t";

	protected final String TEXT_1025 = " old";

	protected final String TEXT_1026 = " = ";

	protected final String TEXT_1027 = ";";

	protected final String TEXT_1028 = NL + "\t\tboolean isSetChange = old";

	protected final String TEXT_1029 = " != EVIRTUAL_NO_VALUE;";

	protected final String TEXT_1030 = NL + "\t\tboolean old";

	protected final String TEXT_1031 = "ESet = (";

	protected final String TEXT_1032 = " & ";

	protected final String TEXT_1033 = "_ESETFLAG) != 0;";

	protected final String TEXT_1034 = NL + "\t\tboolean old";

	protected final String TEXT_1035 = "ESet = ";

	protected final String TEXT_1036 = "ESet;";

	protected final String TEXT_1037 = NL + "\t\t";

	protected final String TEXT_1038 = " = null;";

	protected final String TEXT_1039 = NL + "\t\t";

	protected final String TEXT_1040 = " &= ~";

	protected final String TEXT_1041 = "_ESETFLAG;";

	protected final String TEXT_1042 = NL + "\t\t";

	protected final String TEXT_1043 = "ESet = false;";

	protected final String TEXT_1044 = NL + "\t\tif (eNotificationRequired())" + NL + "\t\t\teNotify(new ";

	protected final String TEXT_1045 = "(this, ";

	protected final String TEXT_1046 = ".UNSET, ";

	protected final String TEXT_1047 = ", ";

	protected final String TEXT_1048 = "isSetChange ? old";

	protected final String TEXT_1049 = " : null";

	protected final String TEXT_1050 = "old";

	protected final String TEXT_1051 = ", null, ";

	protected final String TEXT_1052 = "isSetChange";

	protected final String TEXT_1053 = "old";

	protected final String TEXT_1054 = "ESet";

	protected final String TEXT_1055 = "));";

	protected final String TEXT_1056 = NL + "\t\tif (";

	protected final String TEXT_1057 = ") ";

	protected final String TEXT_1058 = " |= ";

	protected final String TEXT_1059 = "_EFLAG; else ";

	protected final String TEXT_1060 = " &= ~";

	protected final String TEXT_1061 = "_EFLAG;";

	protected final String TEXT_1062 = NL + "\t\t";

	protected final String TEXT_1063 = " = ";

	protected final String TEXT_1064 = " & ~";

	protected final String TEXT_1065 = "_EFLAG | ";

	protected final String TEXT_1066 = "_EFLAG_DEFAULT;";

	protected final String TEXT_1067 = NL + "\t\t";

	protected final String TEXT_1068 = " = ";

	protected final String TEXT_1069 = ";";

	protected final String TEXT_1070 = NL + "\t\t";

	protected final String TEXT_1071 = " &= ~";

	protected final String TEXT_1072 = "_ESETFLAG;";

	protected final String TEXT_1073 = NL + "\t\t";

	protected final String TEXT_1074 = "ESet = false;";

	protected final String TEXT_1075 = NL + "\t\tif (eNotificationRequired())" + NL + "\t\t\teNotify(new ";

	protected final String TEXT_1076 = "(this, ";

	protected final String TEXT_1077 = ".UNSET, ";

	protected final String TEXT_1078 = ", ";

	protected final String TEXT_1079 = "isSetChange ? old";

	protected final String TEXT_1080 = " : ";

	protected final String TEXT_1081 = "old";

	protected final String TEXT_1082 = ", ";

	protected final String TEXT_1083 = ", ";

	protected final String TEXT_1084 = "isSetChange";

	protected final String TEXT_1085 = "old";

	protected final String TEXT_1086 = "ESet";

	protected final String TEXT_1087 = "));";

	protected final String TEXT_1088 = NL + "\t\t((";

	protected final String TEXT_1089 = ".Internal)((";

	protected final String TEXT_1090 = ".Internal.Wrapper)get";

	protected final String TEXT_1091 = "()).featureMap()).clear(";

	protected final String TEXT_1092 = ");";

	protected final String TEXT_1093 = NL + "\t\t((";

	protected final String TEXT_1094 = ".Internal)get";

	protected final String TEXT_1095 = "()).clear(";

	protected final String TEXT_1096 = ");";

	protected final String TEXT_1097 = NL + "\t\t";

	protected final String TEXT_1098 = NL + "\t\t// TODO: implement this method to unset the '";

	protected final String TEXT_1099 = "' ";

	protected final String TEXT_1100 = NL + "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL
			+ "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_1101 = NL + "\t}" + NL;

	protected final String TEXT_1102 = NL + "\t/**" + NL + "\t * Returns whether the value of the '{@link ";

	protected final String TEXT_1103 = "#";

	protected final String TEXT_1104 = " <em>";

	protected final String TEXT_1105 = "</em>}' ";

	protected final String TEXT_1106 = " is set.";

	protected final String TEXT_1107 = NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL
			+ "\t * @return whether the value of the '<em>";

	protected final String TEXT_1108 = "</em>' ";

	protected final String TEXT_1109 = " is set.";

	protected final String TEXT_1110 = NL + "\t * @see #unset";

	protected final String TEXT_1111 = "()";

	protected final String TEXT_1112 = NL + "\t * @see #";

	protected final String TEXT_1113 = "()";

	protected final String TEXT_1114 = NL + "\t * @see #set";

	protected final String TEXT_1115 = "(";

	protected final String TEXT_1116 = ")";

	protected final String TEXT_1117 = NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1118 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1119 = NL + "\tboolean isSet";

	protected final String TEXT_1120 = "();" + NL;

	protected final String TEXT_1121 = NL + "\tpublic boolean isSet";

	protected final String TEXT_1122 = "_";

	protected final String TEXT_1123 = "()" + NL + "\t{";

	protected final String TEXT_1124 = NL + "\t\treturn eDynamicIsSet(";

	protected final String TEXT_1125 = ", ";

	protected final String TEXT_1126 = ");";

	protected final String TEXT_1127 = NL + "\t\treturn eIsSet(";

	protected final String TEXT_1128 = ");";

	protected final String TEXT_1129 = NL + "\t\treturn ";

	protected final String TEXT_1130 = "__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);";

	protected final String TEXT_1131 = NL + "\t\t";

	protected final String TEXT_1132 = " ";

	protected final String TEXT_1133 = " = (";

	protected final String TEXT_1134 = ")eVirtualGet(";

	protected final String TEXT_1135 = ");";

	protected final String TEXT_1136 = NL + "\t\treturn ";

	protected final String TEXT_1137 = " != null && ((";

	protected final String TEXT_1138 = ".Unsettable";

	protected final String TEXT_1139 = ")";

	protected final String TEXT_1140 = ").isSet();";

	protected final String TEXT_1141 = NL + "\t\treturn eVirtualIsSet(";

	protected final String TEXT_1142 = ");";

	protected final String TEXT_1143 = NL + "\t\treturn (";

	protected final String TEXT_1144 = " & ";

	protected final String TEXT_1145 = "_ESETFLAG) != 0;";

	protected final String TEXT_1146 = NL + "\t\treturn ";

	protected final String TEXT_1147 = "ESet;";

	protected final String TEXT_1148 = NL + "\t\treturn !((";

	protected final String TEXT_1149 = ".Internal)((";

	protected final String TEXT_1150 = ".Internal.Wrapper)get";

	protected final String TEXT_1151 = "()).featureMap()).isEmpty(";

	protected final String TEXT_1152 = ");";

	protected final String TEXT_1153 = NL + "\t\treturn !((";

	protected final String TEXT_1154 = ".Internal)get";

	protected final String TEXT_1155 = "()).isEmpty(";

	protected final String TEXT_1156 = ");";

	protected final String TEXT_1157 = NL + "\t\t";

	protected final String TEXT_1158 = NL + "\t\t// TODO: implement this method to return whether the '";

	protected final String TEXT_1159 = "' ";

	protected final String TEXT_1160 = " is set" + NL
			+ "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL
			+ "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_1161 = NL + "\t}" + NL;

	protected final String TEXT_1162 = NL + "\t/**" + NL + "\t * The cached validation expression for the '{@link #";

	protected final String TEXT_1163 = "(";

	protected final String TEXT_1164 = ") <em>";

	protected final String TEXT_1165 = "</em>}' invariant operation." + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @see #";

	protected final String TEXT_1166 = "(";

	protected final String TEXT_1167 = ")" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */" + NL
			+ "\tprotected static final ";

	protected final String TEXT_1168 = " ";

	protected final String TEXT_1169 = "__EEXPRESSION = \"";

	protected final String TEXT_1170 = "\";";

	protected final String TEXT_1171 = NL;

	protected final String TEXT_1172 = NL + "\t/**" + NL + "\t * The cached invocation delegate for the '{@link #";

	protected final String TEXT_1173 = "(";

	protected final String TEXT_1174 = ") <em>";

	protected final String TEXT_1175 = "</em>}' operation." + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @see #";

	protected final String TEXT_1176 = "(";

	protected final String TEXT_1177 = ")" + NL + "\t * @generated" + NL + "\t * @ordered" + NL + "\t */" + NL
			+ "\tprotected static final ";

	protected final String TEXT_1178 = ".Internal.InvocationDelegate ";

	protected final String TEXT_1179 = "__EINVOCATION_DELEGATE = ((";

	protected final String TEXT_1180 = ".Internal)";

	protected final String TEXT_1181 = ").getInvocationDelegate();" + NL;

	protected final String TEXT_1182 = NL + "\t/**";

	protected final String TEXT_1183 = NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->";

	protected final String TEXT_1184 = NL + "\t * <!-- begin-model-doc -->";

	protected final String TEXT_1185 = NL + "\t * ";

	protected final String TEXT_1186 = NL + "\t * @param ";

	protected final String TEXT_1187 = NL + "\t *   ";

	protected final String TEXT_1188 = NL + "\t * @param ";

	protected final String TEXT_1189 = " ";

	protected final String TEXT_1190 = NL + "\t * <!-- end-model-doc -->";

	protected final String TEXT_1191 = NL + "\t * @model ";

	protected final String TEXT_1192 = NL + "\t *        ";

	protected final String TEXT_1193 = NL + "\t * @model";

	protected final String TEXT_1194 = NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1195 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1196 = NL + "\t";

	protected final String TEXT_1197 = " ";

	protected final String TEXT_1198 = "(";

	protected final String TEXT_1199 = ")";

	protected final String TEXT_1200 = ";" + NL;

	protected final String TEXT_1201 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_1202 = NL + "\tpublic ";

	protected final String TEXT_1203 = " ";

	protected final String TEXT_1204 = "(";

	protected final String TEXT_1205 = ")";

	protected final String TEXT_1206 = NL + "\t{";

	protected final String TEXT_1207 = NL + "\t\t";

	protected final String TEXT_1208 = NL + "\t\treturn" + NL + "\t\t\t";

	protected final String TEXT_1209 = ".validate" + NL + "\t\t\t\t(";

	protected final String TEXT_1210 = "," + NL + "\t\t\t\t this," + NL + "\t\t\t\t ";

	protected final String TEXT_1211 = "," + NL + "\t\t\t\t ";

	protected final String TEXT_1212 = "," + NL + "\t\t\t\t \"";

	protected final String TEXT_1213 = "\",";

	protected final String TEXT_1214 = NL + "\t\t\t\t ";

	protected final String TEXT_1215 = "," + NL + "\t\t\t\t ";

	protected final String TEXT_1216 = "__EEXPRESSION," + NL + "\t\t\t\t ";

	protected final String TEXT_1217 = ".ERROR," + NL + "\t\t\t\t ";

	protected final String TEXT_1218 = ".DIAGNOSTIC_SOURCE," + NL + "\t\t\t\t ";

	protected final String TEXT_1219 = ".";

	protected final String TEXT_1220 = ");";

	protected final String TEXT_1221 = NL + "\t\t// TODO: implement this method" + NL
			+ "\t\t// -> specify the condition that violates the invariant" + NL
			+ "\t\t// -> verify the details of the diagnostic, including severity and message" + NL
			+ "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL + "\t\tif (false)" + NL
			+ "\t\t{" + NL + "\t\t\tif (";

	protected final String TEXT_1222 = " != null)" + NL + "\t\t\t{" + NL + "\t\t\t\t";

	protected final String TEXT_1223 = ".add" + NL + "\t\t\t\t\t(new ";

	protected final String TEXT_1224 = NL + "\t\t\t\t\t\t(";

	protected final String TEXT_1225 = ".ERROR," + NL + "\t\t\t\t\t\t ";

	protected final String TEXT_1226 = ".DIAGNOSTIC_SOURCE," + NL + "\t\t\t\t\t\t ";

	protected final String TEXT_1227 = ".";

	protected final String TEXT_1228 = "," + NL + "\t\t\t\t\t\t ";

	protected final String TEXT_1229 = ".INSTANCE.getString(\"_UI_GenericInvariant_diagnostic\", new Object[] { \"";

	protected final String TEXT_1230 = "\", ";

	protected final String TEXT_1231 = ".getObjectLabel(this, ";

	protected final String TEXT_1232 = ") }),";

	protected final String TEXT_1233 = NL + "\t\t\t\t\t\t new Object [] { this }));" + NL + "\t\t\t}" + NL
			+ "\t\t\treturn false;" + NL + "\t\t}" + NL + "\t\treturn true;";

	protected final String TEXT_1234 = NL + "\t\ttry" + NL + "\t\t{";

	protected final String TEXT_1235 = NL + "\t\t\t";

	protected final String TEXT_1236 = "__EINVOCATION_DELEGATE.dynamicInvoke(this, ";

	protected final String TEXT_1237 = "new ";

	protected final String TEXT_1238 = ".UnmodifiableEList<Object>(";

	protected final String TEXT_1239 = ", ";

	protected final String TEXT_1240 = ")";

	protected final String TEXT_1241 = "null";

	protected final String TEXT_1242 = ");";

	protected final String TEXT_1243 = NL + "\t\t\treturn ";

	protected final String TEXT_1244 = "(";

	protected final String TEXT_1245 = "(";

	protected final String TEXT_1246 = ")";

	protected final String TEXT_1247 = "__EINVOCATION_DELEGATE.dynamicInvoke(this, ";

	protected final String TEXT_1248 = "new ";

	protected final String TEXT_1249 = ".UnmodifiableEList<Object>(";

	protected final String TEXT_1250 = ", ";

	protected final String TEXT_1251 = ")";

	protected final String TEXT_1252 = "null";

	protected final String TEXT_1253 = ")";

	protected final String TEXT_1254 = ").";

	protected final String TEXT_1255 = "()";

	protected final String TEXT_1256 = ";";

	protected final String TEXT_1257 = NL + "\t\t}" + NL + "\t\tcatch (";

	protected final String TEXT_1258 = " ite)" + NL + "\t\t{" + NL + "\t\t\tthrow new ";

	protected final String TEXT_1259 = "(ite);" + NL + "\t\t}";

	protected final String TEXT_1260 = NL + "\t\t// TODO: implement this method" + NL
			+ "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL
			+ "\t\tthrow new UnsupportedOperationException();";

	protected final String TEXT_1261 = NL + "\t}" + NL;

	protected final String TEXT_1262 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1263 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_1264 = NL + "\t@Override";

	protected final String TEXT_1265 = NL + "\tpublic ";

	protected final String TEXT_1266 = " eInverseAdd(";

	protected final String TEXT_1267 = " otherEnd, int featureID, ";

	protected final String TEXT_1268 = " msgs)" + NL + "\t{" + NL + "\t\tswitch (featureID";

	protected final String TEXT_1269 = ")" + NL + "\t\t{";

	protected final String TEXT_1270 = NL + "\t\t\tcase ";

	protected final String TEXT_1271 = ":";

	protected final String TEXT_1272 = NL + "\t\t\t\treturn ((";

	protected final String TEXT_1273 = "(";

	protected final String TEXT_1274 = ".InternalMapView";

	protected final String TEXT_1275 = ")";

	protected final String TEXT_1276 = "()).eMap()).basicAdd(otherEnd, msgs);";

	protected final String TEXT_1277 = NL + "\t\t\t\treturn (";

	protected final String TEXT_1278 = "()).basicAdd(otherEnd, msgs);";

	protected final String TEXT_1279 = NL + "\t\t\t\tif (eInternalContainer() != null)" + NL
			+ "\t\t\t\t\tmsgs = eBasicRemoveFromContainer(msgs);";

	protected final String TEXT_1280 = NL + "\t\t\t\treturn basicSet";

	protected final String TEXT_1281 = "((";

	protected final String TEXT_1282 = ")otherEnd, msgs);";

	protected final String TEXT_1283 = NL + "\t\t\t\treturn eBasicSetContainer(otherEnd, ";

	protected final String TEXT_1284 = ", msgs);";

	protected final String TEXT_1285 = NL + "\t\t\t\t";

	protected final String TEXT_1286 = " ";

	protected final String TEXT_1287 = " = (";

	protected final String TEXT_1288 = ")eVirtualGet(";

	protected final String TEXT_1289 = ");";

	protected final String TEXT_1290 = NL + "\t\t\t\t";

	protected final String TEXT_1291 = " ";

	protected final String TEXT_1292 = " = ";

	protected final String TEXT_1293 = "basicGet";

	protected final String TEXT_1294 = "();";

	protected final String TEXT_1295 = NL + "\t\t\t\tif (";

	protected final String TEXT_1296 = " != null)";

	protected final String TEXT_1297 = NL + "\t\t\t\t\tmsgs = ((";

	protected final String TEXT_1298 = ")";

	protected final String TEXT_1299 = ").eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ";

	protected final String TEXT_1300 = ", null, msgs);";

	protected final String TEXT_1301 = NL + "\t\t\t\t\tmsgs = ((";

	protected final String TEXT_1302 = ")";

	protected final String TEXT_1303 = ").eInverseRemove(this, ";

	protected final String TEXT_1304 = ", ";

	protected final String TEXT_1305 = ".class, msgs);";

	protected final String TEXT_1306 = NL + "\t\t\t\treturn basicSet";

	protected final String TEXT_1307 = "((";

	protected final String TEXT_1308 = ")otherEnd, msgs);";

	protected final String TEXT_1309 = NL + "\t\t}";

	protected final String TEXT_1310 = NL + "\t\treturn super.eInverseAdd(otherEnd, featureID, msgs);";

	protected final String TEXT_1311 = NL + "\t\treturn eDynamicInverseAdd(otherEnd, featureID, msgs);";

	protected final String TEXT_1312 = NL + "\t}" + NL;

	protected final String TEXT_1313 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1314 = NL + "\t@Override";

	protected final String TEXT_1315 = NL + "\tpublic ";

	protected final String TEXT_1316 = " eInverseRemove(";

	protected final String TEXT_1317 = " otherEnd, int featureID, ";

	protected final String TEXT_1318 = " msgs)" + NL + "\t{" + NL + "\t\tswitch (featureID";

	protected final String TEXT_1319 = ")" + NL + "\t\t{";

	protected final String TEXT_1320 = NL + "\t\t\tcase ";

	protected final String TEXT_1321 = ":";

	protected final String TEXT_1322 = NL + "\t\t\t\treturn ((";

	protected final String TEXT_1323 = ")((";

	protected final String TEXT_1324 = ".InternalMapView";

	protected final String TEXT_1325 = ")";

	protected final String TEXT_1326 = "()).eMap()).basicRemove(otherEnd, msgs);";

	protected final String TEXT_1327 = NL + "\t\t\t\treturn ((";

	protected final String TEXT_1328 = ")((";

	protected final String TEXT_1329 = ".Internal.Wrapper)";

	protected final String TEXT_1330 = "()).featureMap()).basicRemove(otherEnd, msgs);";

	protected final String TEXT_1331 = NL + "\t\t\t\treturn ((";

	protected final String TEXT_1332 = ")";

	protected final String TEXT_1333 = "()).basicRemove(otherEnd, msgs);";

	protected final String TEXT_1334 = NL + "\t\t\t\treturn eBasicSetContainer(null, ";

	protected final String TEXT_1335 = ", msgs);";

	protected final String TEXT_1336 = NL + "\t\t\t\treturn basicUnset";

	protected final String TEXT_1337 = "(msgs);";

	protected final String TEXT_1338 = NL + "\t\t\t\treturn basicSet";

	protected final String TEXT_1339 = "(null, msgs);";

	protected final String TEXT_1340 = NL + "\t\t}";

	protected final String TEXT_1341 = NL + "\t\treturn super.eInverseRemove(otherEnd, featureID, msgs);";

	protected final String TEXT_1342 = NL + "\t\treturn eDynamicInverseRemove(otherEnd, featureID, msgs);";

	protected final String TEXT_1343 = NL + "\t}" + NL;

	protected final String TEXT_1344 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1345 = NL + "\t@Override";

	protected final String TEXT_1346 = NL + "\tpublic ";

	protected final String TEXT_1347 = " eBasicRemoveFromContainerFeature(";

	protected final String TEXT_1348 = " msgs)" + NL + "\t{" + NL + "\t\tswitch (eContainerFeatureID()";

	protected final String TEXT_1349 = ")" + NL + "\t\t{";

	protected final String TEXT_1350 = NL + "\t\t\tcase ";

	protected final String TEXT_1351 = ":" + NL + "\t\t\t\treturn eInternalContainer().eInverseRemove(this, ";

	protected final String TEXT_1352 = ", ";

	protected final String TEXT_1353 = ".class, msgs);";

	protected final String TEXT_1354 = NL + "\t\t}";

	protected final String TEXT_1355 = NL + "\t\treturn super.eBasicRemoveFromContainerFeature(msgs);";

	protected final String TEXT_1356 = NL + "\t\treturn eDynamicBasicRemoveFromContainer(msgs);";

	protected final String TEXT_1357 = NL + "\t}" + NL;

	protected final String TEXT_1358 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1359 = NL + "\t@Override";

	protected final String TEXT_1360 = NL + "\tpublic Object eGet(int featureID, boolean resolve, boolean coreType)"
			+ NL + "\t{" + NL + "\t\tswitch (featureID";

	protected final String TEXT_1361 = ")" + NL + "\t\t{";

	protected final String TEXT_1362 = NL + "\t\t\tcase ";

	protected final String TEXT_1363 = ":";

	protected final String TEXT_1364 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1365 = "();";

	protected final String TEXT_1366 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1367 = "() ? Boolean.TRUE : Boolean.FALSE;";

	protected final String TEXT_1368 = NL + "\t\t\t\treturn new ";

	protected final String TEXT_1369 = "(";

	protected final String TEXT_1370 = "());";

	protected final String TEXT_1371 = NL + "\t\t\t\tif (resolve) return ";

	protected final String TEXT_1372 = "();" + NL + "\t\t\t\treturn basicGet";

	protected final String TEXT_1373 = "();";

	protected final String TEXT_1374 = NL + "\t\t\t\tif (coreType) return ((";

	protected final String TEXT_1375 = ".InternalMapView";

	protected final String TEXT_1376 = ")";

	protected final String TEXT_1377 = "()).eMap();" + NL + "\t\t\t\telse return ";

	protected final String TEXT_1378 = "();";

	protected final String TEXT_1379 = NL + "\t\t\t\tif (coreType) return ";

	protected final String TEXT_1380 = "();" + NL + "\t\t\t\telse return ";

	protected final String TEXT_1381 = "().map();";

	protected final String TEXT_1382 = NL + "\t\t\t\tif (coreType) return ((";

	protected final String TEXT_1383 = ".Internal.Wrapper)";

	protected final String TEXT_1384 = "()).featureMap();" + NL + "\t\t\t\treturn ";

	protected final String TEXT_1385 = "();";

	protected final String TEXT_1386 = NL + "\t\t\t\tif (coreType) return ";

	protected final String TEXT_1387 = "();" + NL + "\t\t\t\treturn ((";

	protected final String TEXT_1388 = ".Internal)";

	protected final String TEXT_1389 = "()).getWrapper();";

	protected final String TEXT_1390 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1391 = "();";

	protected final String TEXT_1392 = NL + "\t\t}";

	protected final String TEXT_1393 = NL + "\t\treturn super.eGet(featureID, resolve, coreType);";

	protected final String TEXT_1394 = NL + "\t\treturn eDynamicGet(featureID, resolve, coreType);";

	protected final String TEXT_1395 = NL + "\t}" + NL;

	protected final String TEXT_1396 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1397 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_1398 = NL + "\t@Override";

	protected final String TEXT_1399 = NL + "\tpublic void eSet(int featureID, Object newValue)" + NL + "\t{" + NL
			+ "\t\tswitch (featureID";

	protected final String TEXT_1400 = ")" + NL + "\t\t{";

	protected final String TEXT_1401 = NL + "\t\t\tcase ";

	protected final String TEXT_1402 = ":";

	protected final String TEXT_1403 = NL + "\t\t\t\t((";

	protected final String TEXT_1404 = ".Internal)((";

	protected final String TEXT_1405 = ".Internal.Wrapper)";

	protected final String TEXT_1406 = "()).featureMap()).set(newValue);";

	protected final String TEXT_1407 = NL + "\t\t\t\t((";

	protected final String TEXT_1408 = ".Internal)";

	protected final String TEXT_1409 = "()).set(newValue);";

	protected final String TEXT_1410 = NL + "\t\t\t\t((";

	protected final String TEXT_1411 = ".Setting)((";

	protected final String TEXT_1412 = ".InternalMapView";

	protected final String TEXT_1413 = ")";

	protected final String TEXT_1414 = "()).eMap()).set(newValue);";

	protected final String TEXT_1415 = NL + "\t\t\t\t((";

	protected final String TEXT_1416 = ".Setting)";

	protected final String TEXT_1417 = "()).set(newValue);";

	protected final String TEXT_1418 = NL + "\t\t\t\t";

	protected final String TEXT_1419 = "().clear();" + NL + "\t\t\t\t";

	protected final String TEXT_1420 = "().addAll((";

	protected final String TEXT_1421 = "<? extends ";

	protected final String TEXT_1422 = ">";

	protected final String TEXT_1423 = ")newValue);";

	protected final String TEXT_1424 = NL + "\t\t\t\tset";

	protected final String TEXT_1425 = "(((";

	protected final String TEXT_1426 = ")newValue).";

	protected final String TEXT_1427 = "());";

	protected final String TEXT_1428 = NL + "\t\t\t\tset";

	protected final String TEXT_1429 = "(";

	protected final String TEXT_1430 = "(";

	protected final String TEXT_1431 = ")";

	protected final String TEXT_1432 = "newValue);";

	protected final String TEXT_1433 = NL + "\t\t\t\treturn;";

	protected final String TEXT_1434 = NL + "\t\t}";

	protected final String TEXT_1435 = NL + "\t\tsuper.eSet(featureID, newValue);";

	protected final String TEXT_1436 = NL + "\t\teDynamicSet(featureID, newValue);";

	protected final String TEXT_1437 = NL + "\t}" + NL;

	protected final String TEXT_1438 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1439 = NL + "\t@Override";

	protected final String TEXT_1440 = NL + "\tpublic void eUnset(int featureID)" + NL + "\t{" + NL
			+ "\t\tswitch (featureID";

	protected final String TEXT_1441 = ")" + NL + "\t\t{";

	protected final String TEXT_1442 = NL + "\t\t\tcase ";

	protected final String TEXT_1443 = ":";

	protected final String TEXT_1444 = NL + "\t\t\t\t((";

	protected final String TEXT_1445 = ".Internal.Wrapper)";

	protected final String TEXT_1446 = "()).featureMap().clear();";

	protected final String TEXT_1447 = NL + "\t\t\t\t";

	protected final String TEXT_1448 = "().clear();";

	protected final String TEXT_1449 = NL + "\t\t\t\tunset";

	protected final String TEXT_1450 = "();";

	protected final String TEXT_1451 = NL + "\t\t\t\tset";

	protected final String TEXT_1452 = "((";

	protected final String TEXT_1453 = ")null);";

	protected final String TEXT_1454 = NL + "\t\t\t\tset";

	protected final String TEXT_1455 = "(";

	protected final String TEXT_1456 = ");";

	protected final String TEXT_1457 = NL + "\t\t\t\treturn;";

	protected final String TEXT_1458 = NL + "\t\t}";

	protected final String TEXT_1459 = NL + "\t\tsuper.eUnset(featureID);";

	protected final String TEXT_1460 = NL + "\t\teDynamicUnset(featureID);";

	protected final String TEXT_1461 = NL + "\t}" + NL;

	protected final String TEXT_1462 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1463 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_1464 = NL + "\t@Override";

	protected final String TEXT_1465 = NL + "\tpublic boolean eIsSet(int featureID)" + NL + "\t{" + NL
			+ "\t\tswitch (featureID";

	protected final String TEXT_1466 = ")" + NL + "\t\t{";

	protected final String TEXT_1467 = NL + "\t\t\tcase ";

	protected final String TEXT_1468 = ":";

	protected final String TEXT_1469 = NL + "\t\t\t\treturn isSet";

	protected final String TEXT_1470 = "();";

	protected final String TEXT_1471 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1472 = "__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);";

	protected final String TEXT_1473 = NL + "\t\t\t\treturn !((";

	protected final String TEXT_1474 = ".Internal.Wrapper)";

	protected final String TEXT_1475 = "()).featureMap().isEmpty();";

	protected final String TEXT_1476 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1477 = " != null && !";

	protected final String TEXT_1478 = ".featureMap().isEmpty();";

	protected final String TEXT_1479 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1480 = " != null && !";

	protected final String TEXT_1481 = ".isEmpty();";

	protected final String TEXT_1482 = NL + "\t\t\t\t";

	protected final String TEXT_1483 = " ";

	protected final String TEXT_1484 = " = (";

	protected final String TEXT_1485 = ")eVirtualGet(";

	protected final String TEXT_1486 = ");" + NL + "\t\t\t\treturn ";

	protected final String TEXT_1487 = " != null && !";

	protected final String TEXT_1488 = ".isEmpty();";

	protected final String TEXT_1489 = NL + "\t\t\t\treturn !";

	protected final String TEXT_1490 = "().isEmpty();";

	protected final String TEXT_1491 = NL + "\t\t\t\treturn isSet";

	protected final String TEXT_1492 = "();";

	protected final String TEXT_1493 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1494 = " != null;";

	protected final String TEXT_1495 = NL + "\t\t\t\treturn eVirtualGet(";

	protected final String TEXT_1496 = ") != null;";

	protected final String TEXT_1497 = NL + "\t\t\t\treturn basicGet";

	protected final String TEXT_1498 = "() != null;";

	protected final String TEXT_1499 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1500 = " != null;";

	protected final String TEXT_1501 = NL + "\t\t\t\treturn eVirtualGet(";

	protected final String TEXT_1502 = ") != null;";

	protected final String TEXT_1503 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1504 = "() != null;";

	protected final String TEXT_1505 = NL + "\t\t\t\treturn ((";

	protected final String TEXT_1506 = " & ";

	protected final String TEXT_1507 = "_EFLAG) != 0) != ";

	protected final String TEXT_1508 = ";";

	protected final String TEXT_1509 = NL + "\t\t\t\treturn (";

	protected final String TEXT_1510 = " & ";

	protected final String TEXT_1511 = "_EFLAG) != ";

	protected final String TEXT_1512 = "_EFLAG_DEFAULT;";

	protected final String TEXT_1513 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1514 = " != ";

	protected final String TEXT_1515 = ";";

	protected final String TEXT_1516 = NL + "\t\t\t\treturn eVirtualGet(";

	protected final String TEXT_1517 = ", ";

	protected final String TEXT_1518 = ") != ";

	protected final String TEXT_1519 = ";";

	protected final String TEXT_1520 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1521 = "() != ";

	protected final String TEXT_1522 = ";";

	protected final String TEXT_1523 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1524 = " == null ? ";

	protected final String TEXT_1525 = " != null : !";

	protected final String TEXT_1526 = ".equals(";

	protected final String TEXT_1527 = ");";

	protected final String TEXT_1528 = NL + "\t\t\t\t";

	protected final String TEXT_1529 = " ";

	protected final String TEXT_1530 = " = (";

	protected final String TEXT_1531 = ")eVirtualGet(";

	protected final String TEXT_1532 = ", ";

	protected final String TEXT_1533 = ");" + NL + "\t\t\t\treturn ";

	protected final String TEXT_1534 = " == null ? ";

	protected final String TEXT_1535 = " != null : !";

	protected final String TEXT_1536 = ".equals(";

	protected final String TEXT_1537 = ");";

	protected final String TEXT_1538 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1539 = " == null ? ";

	protected final String TEXT_1540 = "() != null : !";

	protected final String TEXT_1541 = ".equals(";

	protected final String TEXT_1542 = "());";

	protected final String TEXT_1543 = NL + "\t\t}";

	protected final String TEXT_1544 = NL + "\t\treturn super.eIsSet(featureID);";

	protected final String TEXT_1545 = NL + "\t\treturn eDynamicIsSet(featureID);";

	protected final String TEXT_1546 = NL + "\t}" + NL;

	protected final String TEXT_1547 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1548 = NL + "\t@Override";

	protected final String TEXT_1549 = NL + "\tpublic int eBaseStructuralFeatureID(int derivedFeatureID, Class";

	protected final String TEXT_1550 = " baseClass)" + NL + "\t{";

	protected final String TEXT_1551 = NL + "\t\tif (baseClass == ";

	protected final String TEXT_1552 = ".class)" + NL + "\t\t{" + NL + "\t\t\tswitch (derivedFeatureID";

	protected final String TEXT_1553 = ")" + NL + "\t\t\t{";

	protected final String TEXT_1554 = NL + "\t\t\t\tcase ";

	protected final String TEXT_1555 = ": return ";

	protected final String TEXT_1556 = ";";

	protected final String TEXT_1557 = NL + "\t\t\t\tdefault: return -1;" + NL + "\t\t\t}" + NL + "\t\t}";

	protected final String TEXT_1558 = NL + "\t\treturn super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);"
			+ NL + "\t}";

	protected final String TEXT_1559 = NL + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1560 = NL + "\t@Override";

	protected final String TEXT_1561 = NL + "\tpublic int eDerivedStructuralFeatureID(int baseFeatureID, Class";

	protected final String TEXT_1562 = " baseClass)" + NL + "\t{";

	protected final String TEXT_1563 = NL + "\t\tif (baseClass == ";

	protected final String TEXT_1564 = ".class)" + NL + "\t\t{" + NL + "\t\t\tswitch (baseFeatureID)" + NL + "\t\t\t{";

	protected final String TEXT_1565 = NL + "\t\t\t\tcase ";

	protected final String TEXT_1566 = ": return ";

	protected final String TEXT_1567 = ";";

	protected final String TEXT_1568 = NL + "\t\t\t\tdefault: return -1;" + NL + "\t\t\t}" + NL + "\t\t}";

	protected final String TEXT_1569 = NL + "\t\tif (baseClass == ";

	protected final String TEXT_1570 = ".class)" + NL + "\t\t{" + NL + "\t\t\tswitch (baseFeatureID";

	protected final String TEXT_1571 = ")" + NL + "\t\t\t{";

	protected final String TEXT_1572 = NL + "\t\t\t\tcase ";

	protected final String TEXT_1573 = ": return ";

	protected final String TEXT_1574 = ";";

	protected final String TEXT_1575 = NL + "\t\t\t\tdefault: return -1;" + NL + "\t\t\t}" + NL + "\t\t}";

	protected final String TEXT_1576 = NL + "\t\treturn super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);"
			+ NL + "\t}" + NL;

	protected final String TEXT_1577 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1578 = NL + "\t@Override";

	protected final String TEXT_1579 = NL + "\tpublic int eDerivedOperationID(int baseOperationID, Class";

	protected final String TEXT_1580 = " baseClass)" + NL + "\t{";

	protected final String TEXT_1581 = NL + "\t\tif (baseClass == ";

	protected final String TEXT_1582 = ".class)" + NL + "\t\t{" + NL + "\t\t\tswitch (baseOperationID)" + NL
			+ "\t\t\t{";

	protected final String TEXT_1583 = NL + "\t\t\t\tcase ";

	protected final String TEXT_1584 = ": return ";

	protected final String TEXT_1585 = ";";

	protected final String TEXT_1586 = NL
			+ "\t\t\t\tdefault: return super.eDerivedOperationID(baseOperationID, baseClass);" + NL + "\t\t\t}" + NL
			+ "\t\t}";

	protected final String TEXT_1587 = NL + "\t\tif (baseClass == ";

	protected final String TEXT_1588 = ".class)" + NL + "\t\t{" + NL + "\t\t\tswitch (baseOperationID)" + NL
			+ "\t\t\t{";

	protected final String TEXT_1589 = NL + "\t\t\t\tcase ";

	protected final String TEXT_1590 = ": return ";

	protected final String TEXT_1591 = ";";

	protected final String TEXT_1592 = NL + "\t\t\t\tdefault: return -1;" + NL + "\t\t\t}" + NL + "\t\t}";

	protected final String TEXT_1593 = NL + "\t\tif (baseClass == ";

	protected final String TEXT_1594 = ".class)" + NL + "\t\t{" + NL + "\t\t\tswitch (baseOperationID";

	protected final String TEXT_1595 = ")" + NL + "\t\t\t{";

	protected final String TEXT_1596 = NL + "\t\t\t\tcase ";

	protected final String TEXT_1597 = ": return ";

	protected final String TEXT_1598 = ";";

	protected final String TEXT_1599 = NL + "\t\t\t\tdefault: return -1;" + NL + "\t\t\t}" + NL + "\t\t}";

	protected final String TEXT_1600 = NL + "\t\treturn super.eDerivedOperationID(baseOperationID, baseClass);" + NL
			+ "\t}" + NL;

	protected final String TEXT_1601 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1602 = NL + "\t@Override";

	protected final String TEXT_1603 = NL + "\tprotected Object[] eVirtualValues()" + NL + "\t{" + NL + "\t\treturn ";

	protected final String TEXT_1604 = ";" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->"
			+ NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1605 = NL + "\t@Override";

	protected final String TEXT_1606 = NL + "\tprotected void eSetVirtualValues(Object[] newValues)" + NL + "\t{" + NL
			+ "\t\t";

	protected final String TEXT_1607 = " = newValues;" + NL + "\t}" + NL;

	protected final String TEXT_1608 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1609 = NL + "\t@Override";

	protected final String TEXT_1610 = NL + "\tprotected int eVirtualIndexBits(int offset)" + NL + "\t{" + NL
			+ "\t\tswitch (offset)" + NL + "\t\t{";

	protected final String TEXT_1611 = NL + "\t\t\tcase ";

	protected final String TEXT_1612 = " :" + NL + "\t\t\t\treturn ";

	protected final String TEXT_1613 = ";";

	protected final String TEXT_1614 = NL + "\t\t\tdefault :" + NL + "\t\t\t\tthrow new IndexOutOfBoundsException();"
			+ NL + "\t\t}" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1615 = NL + "\t@Override";

	protected final String TEXT_1616 = NL + "\tprotected void eSetVirtualIndexBits(int offset, int newIndexBits)" + NL
			+ "\t{" + NL + "\t\tswitch (offset)" + NL + "\t\t{";

	protected final String TEXT_1617 = NL + "\t\t\tcase ";

	protected final String TEXT_1618 = " :" + NL + "\t\t\t\t";

	protected final String TEXT_1619 = " = newIndexBits;" + NL + "\t\t\t\tbreak;";

	protected final String TEXT_1620 = NL + "\t\t\tdefault :" + NL + "\t\t\t\tthrow new IndexOutOfBoundsException();"
			+ NL + "\t\t}" + NL + "\t}" + NL;

	protected final String TEXT_1621 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1622 = NL + "\t@Override";

	protected final String TEXT_1623 = NL + "\t@SuppressWarnings(";

	protected final String TEXT_1624 = "\"unchecked\"";

	protected final String TEXT_1625 = "{\"rawtypes\", \"unchecked\" }";

	protected final String TEXT_1626 = ")";

	protected final String TEXT_1627 = NL + "\tpublic Object eInvoke(int operationID, ";

	protected final String TEXT_1628 = " arguments) throws ";

	protected final String TEXT_1629 = NL + "\t{" + NL + "\t\tswitch (operationID";

	protected final String TEXT_1630 = ")" + NL + "\t\t{";

	protected final String TEXT_1631 = NL + "\t\t\tcase ";

	protected final String TEXT_1632 = ":";

	protected final String TEXT_1633 = NL + "\t\t\t\t";

	protected final String TEXT_1634 = "(";

	protected final String TEXT_1635 = "(";

	protected final String TEXT_1636 = "(";

	protected final String TEXT_1637 = ")";

	protected final String TEXT_1638 = "arguments.get(";

	protected final String TEXT_1639 = ")";

	protected final String TEXT_1640 = ").";

	protected final String TEXT_1641 = "()";

	protected final String TEXT_1642 = ", ";

	protected final String TEXT_1643 = ");" + NL + "\t\t\t\treturn null;";

	protected final String TEXT_1644 = NL + "\t\t\t\treturn ";

	protected final String TEXT_1645 = "new ";

	protected final String TEXT_1646 = "(";

	protected final String TEXT_1647 = "(";

	protected final String TEXT_1648 = "(";

	protected final String TEXT_1649 = "(";

	protected final String TEXT_1650 = ")";

	protected final String TEXT_1651 = "arguments.get(";

	protected final String TEXT_1652 = ")";

	protected final String TEXT_1653 = ").";

	protected final String TEXT_1654 = "()";

	protected final String TEXT_1655 = ", ";

	protected final String TEXT_1656 = ")";

	protected final String TEXT_1657 = ")";

	protected final String TEXT_1658 = ";";

	protected final String TEXT_1659 = NL + "\t\t}";

	protected final String TEXT_1660 = NL + "\t\treturn super.eInvoke(operationID, arguments);";

	protected final String TEXT_1661 = NL + "\t\treturn eDynamicInvoke(operationID, arguments);";

	protected final String TEXT_1662 = NL + "\t}" + NL;

	protected final String TEXT_1663 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1664 = NL + "\t@Override";

	protected final String TEXT_1665 = NL + "\tpublic String toString()" + NL + "\t{" + NL
			+ "\t\tif (eIsProxy()) return super.toString();" + NL + "" + NL
			+ "\t\tStringBuffer result = new StringBuffer(super.toString());";

	protected final String TEXT_1666 = NL + "\t\tresult.append(\" (";

	protected final String TEXT_1667 = ": \");";

	protected final String TEXT_1668 = NL + "\t\tresult.append(\", ";

	protected final String TEXT_1669 = ": \");";

	protected final String TEXT_1670 = NL + "\t\tif (eVirtualIsSet(";

	protected final String TEXT_1671 = ")) result.append(eVirtualGet(";

	protected final String TEXT_1672 = ")); else result.append(\"<unset>\");";

	protected final String TEXT_1673 = NL + "\t\tif (";

	protected final String TEXT_1674 = "(";

	protected final String TEXT_1675 = " & ";

	protected final String TEXT_1676 = "_ESETFLAG) != 0";

	protected final String TEXT_1677 = "ESet";

	protected final String TEXT_1678 = ") result.append((";

	protected final String TEXT_1679 = " & ";

	protected final String TEXT_1680 = "_EFLAG) != 0); else result.append(\"<unset>\");";

	protected final String TEXT_1681 = NL + "\t\tif (";

	protected final String TEXT_1682 = "(";

	protected final String TEXT_1683 = " & ";

	protected final String TEXT_1684 = "_ESETFLAG) != 0";

	protected final String TEXT_1685 = "ESet";

	protected final String TEXT_1686 = ") result.append(";

	protected final String TEXT_1687 = "_EFLAG_VALUES[(";

	protected final String TEXT_1688 = " & ";

	protected final String TEXT_1689 = "_EFLAG) >>> ";

	protected final String TEXT_1690 = "_EFLAG_OFFSET]); else result.append(\"<unset>\");";

	protected final String TEXT_1691 = NL + "\t\tif (";

	protected final String TEXT_1692 = "(";

	protected final String TEXT_1693 = " & ";

	protected final String TEXT_1694 = "_ESETFLAG) != 0";

	protected final String TEXT_1695 = "ESet";

	protected final String TEXT_1696 = ") result.append(";

	protected final String TEXT_1697 = "); else result.append(\"<unset>\");";

	protected final String TEXT_1698 = NL + "\t\tresult.append(eVirtualGet(";

	protected final String TEXT_1699 = ", ";

	protected final String TEXT_1700 = "));";

	protected final String TEXT_1701 = NL + "\t\tresult.append((";

	protected final String TEXT_1702 = " & ";

	protected final String TEXT_1703 = "_EFLAG) != 0);";

	protected final String TEXT_1704 = NL + "\t\tresult.append(";

	protected final String TEXT_1705 = "_EFLAG_VALUES[(";

	protected final String TEXT_1706 = " & ";

	protected final String TEXT_1707 = "_EFLAG) >>> ";

	protected final String TEXT_1708 = "_EFLAG_OFFSET]);";

	protected final String TEXT_1709 = NL + "\t\tresult.append(";

	protected final String TEXT_1710 = ");";

	protected final String TEXT_1711 = NL + "\t\tresult.append(')');" + NL + "\t\treturn result.toString();" + NL
			+ "\t}" + NL;

	protected final String TEXT_1712 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";

	protected final String TEXT_1713 = NL + "\t@";

	protected final String TEXT_1714 = NL + "\tprotected int hash = -1;" + NL + "" + NL + "\t/**" + NL
			+ "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */"
			+ NL + "\tpublic int getHash()" + NL + "\t{" + NL + "\t\tif (hash == -1)" + NL + "\t\t{" + NL + "\t\t\t";

	protected final String TEXT_1715 = " theKey = getKey();" + NL
			+ "\t\t\thash = (theKey == null ? 0 : theKey.hashCode());" + NL + "\t\t}" + NL + "\t\treturn hash;" + NL
			+ "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->"
			+ NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic void setHash(int hash)" + NL + "\t{" + NL
			+ "\t\tthis.hash = hash;" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic ";

	protected final String TEXT_1716 = " getKey()" + NL + "\t{";

	protected final String TEXT_1717 = NL + "\t\treturn new ";

	protected final String TEXT_1718 = "(getTypedKey());";

	protected final String TEXT_1719 = NL + "\t\treturn getTypedKey();";

	protected final String TEXT_1720 = NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic void setKey(";

	protected final String TEXT_1721 = " key)" + NL + "\t{";

	protected final String TEXT_1722 = NL + "\t\tgetTypedKey().addAll(";

	protected final String TEXT_1723 = "(";

	protected final String TEXT_1724 = ")";

	protected final String TEXT_1725 = "key);";

	protected final String TEXT_1726 = NL + "\t\tsetTypedKey(key);";

	protected final String TEXT_1727 = NL + "\t\tsetTypedKey(((";

	protected final String TEXT_1728 = ")key).";

	protected final String TEXT_1729 = "());";

	protected final String TEXT_1730 = NL + "\t\tsetTypedKey((";

	protected final String TEXT_1731 = ")key);";

	protected final String TEXT_1732 = NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic ";

	protected final String TEXT_1733 = " getValue()" + NL + "\t{";

	protected final String TEXT_1734 = NL + "\t\treturn new ";

	protected final String TEXT_1735 = "(getTypedValue());";

	protected final String TEXT_1736 = NL + "\t\treturn getTypedValue();";

	protected final String TEXT_1737 = NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL
			+ "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic ";

	protected final String TEXT_1738 = " setValue(";

	protected final String TEXT_1739 = " value)" + NL + "\t{" + NL + "\t\t";

	protected final String TEXT_1740 = " oldValue = getValue();";

	protected final String TEXT_1741 = NL + "\t\tgetTypedValue().clear();" + NL + "\t\tgetTypedValue().addAll(";

	protected final String TEXT_1742 = "(";

	protected final String TEXT_1743 = ")";

	protected final String TEXT_1744 = "value);";

	protected final String TEXT_1745 = NL + "\t\tsetTypedValue(value);";

	protected final String TEXT_1746 = NL + "\t\tsetTypedValue(((";

	protected final String TEXT_1747 = ")value).";

	protected final String TEXT_1748 = "());";

	protected final String TEXT_1749 = NL + "\t\tsetTypedValue((";

	protected final String TEXT_1750 = ")value);";

	protected final String TEXT_1751 = NL + "\t\treturn oldValue;" + NL + "\t}" + NL + "" + NL + "\t/**" + NL
			+ "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL
			+ "\t */";

	protected final String TEXT_1752 = NL + "\t@SuppressWarnings(\"unchecked\")";

	protected final String TEXT_1753 = NL + "\tpublic ";

	protected final String TEXT_1754 = " getEMap()" + NL + "\t{" + NL + "\t\t";

	protected final String TEXT_1755 = " container = eContainer();" + NL + "\t\treturn container == null ? null : (";

	protected final String TEXT_1756 = ")container.eGet(eContainmentFeature());" + NL + "\t}" + NL;

	protected final String TEXT_1757 = NL + "} //";

	protected final String TEXT_1758 = NL;

	public String generate(final Object argument) {
		final StringBuffer stringBuffer = new StringBuffer();

		/*
		 * Copyright (c) 2010-2012 Gergely Varro All rights reserved. This program and
		 * the accompanying materials are made available under the terms of the Eclipse
		 * Public License v1.0 which accompanies this distribution, and is available at
		 * http://www.eclipse.org/legal/epl-v10.html
		 *
		 * Contributors: Gergely Varro - Initial API and implementation
		 */

		/**
		 * Copyright (c) 2002-2011 IBM Corporation and others. All rights reserved. This
		 * program and the accompanying materials are made available under the terms of
		 * the Eclipse Public License v1.0 which accompanies this distribution, and is
		 * available at http://www.eclipse.org/legal/epl-v10.html
		 *
		 * Contributors: IBM - Initial API and implementation
		 */

		final GenClass genClass = (GenClass) ((Object[]) argument)[0];
		final GenPackage genPackage = genClass.getGenPackage();
		final GenModel genModel = genPackage.getGenModel();
		final boolean isJDK50 = genModel.getComplianceLevel().getValue() >= GenJDKLevel.JDK50;
		final boolean isInterface = Boolean.TRUE.equals(((Object[]) argument)[1]);
		final boolean isImplementation = Boolean.TRUE.equals(((Object[]) argument)[2]);
		final boolean isGWT = genModel.getRuntimePlatform() == GenRuntimePlatform.GWT;
		final String publicStaticFinalFlag = isImplementation ? "public static final " : "";
		final String singleWildcard = isJDK50 ? "<?>" : "";
		final String negativeOffsetCorrection = genClass.hasOffsetCorrection()
				? " - " + genClass.getOffsetCorrectionField(null)
				: "";
		final String positiveOffsetCorrection = genClass.hasOffsetCorrection()
				? " + " + genClass.getOffsetCorrectionField(null)
				: "";
		final String negativeOperationOffsetCorrection = genClass.hasOffsetCorrection()
				? " - EOPERATION_OFFSET_CORRECTION"
				: "";
		final String positiveOperationOffsetCorrection = genClass.hasOffsetCorrection()
				? " + EOPERATION_OFFSET_CORRECTION"
				: "";
		stringBuffer.append(TEXT_1);
		{
			GenBase copyrightHolder = argument instanceof GenBase ? (GenBase) argument
					: argument instanceof Object[] && ((Object[]) argument)[0] instanceof GenBase
							? (GenBase) ((Object[]) argument)[0]
							: null;
			if (copyrightHolder != null && copyrightHolder.hasCopyright()) {
				stringBuffer.append(TEXT_2);
				stringBuffer.append(
						copyrightHolder.getCopyright(copyrightHolder.getGenModel().getIndentation(stringBuffer)));
			}
		}
		stringBuffer.append(TEXT_3);
		if (isInterface) {
			stringBuffer.append(TEXT_4);
			stringBuffer.append(genPackage.getInterfacePackageName());
			stringBuffer.append(TEXT_5);
		} else {
			stringBuffer.append(TEXT_6);
			stringBuffer.append(genPackage.getClassPackageName());
			stringBuffer.append(TEXT_7);
		}
		stringBuffer.append(TEXT_8);
		genModel.markImportLocation(stringBuffer, genPackage);
		if (isImplementation) {
			genClass.addClassPsuedoImports();
		}

		stringBuffer.append(TEXT_9);
		if (isInterface) {
			stringBuffer.append(TEXT_10);
			stringBuffer.append(genClass.getFormattedName());
			stringBuffer.append(TEXT_11);
			if (genClass.hasDocumentation()) {
				stringBuffer.append(TEXT_12);
				stringBuffer.append(genClass.getDocumentation(genModel.getIndentation(stringBuffer)));
				stringBuffer.append(TEXT_13);
			}
			stringBuffer.append(TEXT_14);
			if (!genClass.getGenFeatures().isEmpty()) {
				stringBuffer.append(TEXT_15);
				for (GenFeature genFeature : genClass.getGenFeatures()) {
					if (!genFeature.isSuppressedGetVisibility()) {
						stringBuffer.append(TEXT_16);
						stringBuffer.append(genClass.getQualifiedInterfaceName());
						stringBuffer.append(TEXT_17);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_18);
						stringBuffer.append(genFeature.getFormattedName());
						stringBuffer.append(TEXT_19);
					}
				}
				stringBuffer.append(TEXT_20);
			}
			stringBuffer.append(TEXT_21);
			if (!genModel.isSuppressEMFMetaData()) {
				stringBuffer.append(TEXT_22);
				stringBuffer.append(genPackage.getQualifiedPackageInterfaceName());
				stringBuffer.append(TEXT_23);
				stringBuffer.append(genClass.getClassifierAccessorName());
				stringBuffer.append(TEXT_24);
			}
			if (!genModel.isSuppressEMFModelTags()) {
				boolean first = true;
				for (StringTokenizer stringTokenizer = new StringTokenizer(genClass.getModelInfo(),
						"\n\r"); stringTokenizer.hasMoreTokens();) {
					String modelInfo = stringTokenizer.nextToken();
					if (first) {
						first = false;
						stringBuffer.append(TEXT_25);
						stringBuffer.append(modelInfo);
					} else {
						stringBuffer.append(TEXT_26);
						stringBuffer.append(modelInfo);
					}
				}
				if (first) {
					stringBuffer.append(TEXT_27);
				}
			}
			if (genClass.needsRootExtendsInterfaceExtendsTag()) {
				stringBuffer.append(TEXT_28);
				stringBuffer.append(genModel.getImportedName(genModel.getRootExtendsInterface()));
			}
			stringBuffer.append(TEXT_29);
			// Class/interface.javadoc.override.javajetinc
		} else {
			stringBuffer.append(TEXT_30);
			stringBuffer.append(genClass.getFormattedName());
			stringBuffer.append(TEXT_31);
			if (!genClass.getImplementedGenFeatures().isEmpty()) {
				stringBuffer.append(TEXT_32);
				for (GenFeature genFeature : genClass.getImplementedGenFeatures()) {
					stringBuffer.append(TEXT_33);
					stringBuffer.append(genClass.getQualifiedClassName());
					stringBuffer.append(TEXT_34);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_35);
					stringBuffer.append(genFeature.getFormattedName());
					stringBuffer.append(TEXT_36);
				}
				stringBuffer.append(TEXT_37);
			}
			stringBuffer.append(TEXT_38);
		}
		if (isImplementation) {
			stringBuffer.append(TEXT_39);
			if (genClass.isAbstract()) {
				stringBuffer.append(TEXT_40);
			}
			stringBuffer.append(TEXT_41);
			stringBuffer.append(genClass.getClassName());
			stringBuffer.append(genClass.getTypeParameters().trim());
			stringBuffer.append(genClass.getClassExtends());
			stringBuffer.append(genClass.getClassImplements());
		} else {
			stringBuffer.append(TEXT_42);
			stringBuffer.append(genClass.getInterfaceName());
			stringBuffer.append(genClass.getTypeParameters().trim());
			stringBuffer.append(genClass.getInterfaceExtends());
		}
		stringBuffer.append(TEXT_43);
		if (genModel.hasCopyrightField()) {
			stringBuffer.append(TEXT_44);
			stringBuffer.append(publicStaticFinalFlag);
			stringBuffer.append(genModel.getImportedName("java.lang.String"));
			stringBuffer.append(TEXT_45);
			stringBuffer.append(genModel.getCopyrightFieldLiteral());
			stringBuffer.append(TEXT_46);
			stringBuffer.append(genModel.getNonNLS());
			stringBuffer.append(TEXT_47);
		}
		if (isImplementation && genModel.getDriverNumber() != null) {
			stringBuffer.append(TEXT_48);
			stringBuffer.append(genModel.getImportedName("java.lang.String"));
			stringBuffer.append(TEXT_49);
			stringBuffer.append(genModel.getDriverNumber());
			stringBuffer.append(TEXT_50);
			stringBuffer.append(genModel.getNonNLS());
			stringBuffer.append(TEXT_51);
		}
		if (isImplementation && genClass.isJavaIOSerializable()) {
			stringBuffer.append(TEXT_52);
		}
		if (isImplementation && genModel.isVirtualDelegation()) {
			String eVirtualValuesField = genClass.getEVirtualValuesField();
			if (eVirtualValuesField != null) {
				stringBuffer.append(TEXT_53);
				if (isGWT) {
					stringBuffer.append(TEXT_54);
					stringBuffer.append(genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
				}
				stringBuffer.append(TEXT_55);
				stringBuffer.append(eVirtualValuesField);
				stringBuffer.append(TEXT_56);
			}
			{
				List<String> eVirtualIndexBitFields = genClass.getEVirtualIndexBitFields(new ArrayList<String>());
				if (!eVirtualIndexBitFields.isEmpty()) {
					for (String eVirtualIndexBitField : eVirtualIndexBitFields) {
						stringBuffer.append(TEXT_57);
						if (isGWT) {
							stringBuffer.append(TEXT_58);
							stringBuffer
									.append(genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
						}
						stringBuffer.append(TEXT_59);
						stringBuffer.append(eVirtualIndexBitField);
						stringBuffer.append(TEXT_60);
					}
				}
			}
		}
		if (isImplementation && genClass.isModelRoot() && genModel.isBooleanFlagsEnabled()
				&& genModel.getBooleanFlagsReservedBits() == -1) {
			stringBuffer.append(TEXT_61);
			if (isGWT) {
				stringBuffer.append(TEXT_62);
				stringBuffer.append(genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
			}
			stringBuffer.append(TEXT_63);
			stringBuffer.append(genModel.getBooleanFlagsField());
			stringBuffer.append(TEXT_64);
		}
		if (isImplementation && !genModel.isReflectiveDelegation()) {
			for (GenFeature genFeature : genClass.getDeclaredFieldGenFeatures()) {
				if (genFeature.hasSettingDelegate()) {
					stringBuffer.append(TEXT_65);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_66);
					stringBuffer.append(genFeature.getFormattedName());
					stringBuffer.append(TEXT_67);
					stringBuffer.append(genFeature.getFeatureKind());
					stringBuffer.append(TEXT_68);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_69);
					if (isGWT) {
						stringBuffer.append(TEXT_70);
						stringBuffer.append(genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
					}
					stringBuffer.append(TEXT_71);
					stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EStructuralFeature"));
					stringBuffer.append(TEXT_72);
					stringBuffer.append(genFeature.getUpperName());
					stringBuffer.append(TEXT_73);
					stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EStructuralFeature"));
					stringBuffer.append(TEXT_74);
					stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
					stringBuffer.append(TEXT_75);
				} else if (genFeature.isListType() || genFeature.isReferenceType()) {
					if (genClass.isField(genFeature)) {
						stringBuffer.append(TEXT_76);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_77);
						stringBuffer.append(genFeature.getFormattedName());
						stringBuffer.append(TEXT_78);
						stringBuffer.append(genFeature.getFeatureKind());
						stringBuffer.append(TEXT_79);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_80);
						if (isGWT) {
							stringBuffer.append(TEXT_81);
							stringBuffer
									.append(genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
						}
						stringBuffer.append(TEXT_82);
						stringBuffer.append(genFeature.getImportedInternalType(genClass));
						stringBuffer.append(TEXT_83);
						stringBuffer.append(genFeature.getSafeName());
						stringBuffer.append(TEXT_84);
					}
					if (genModel.isArrayAccessors() && genFeature.isListType() && !genFeature.isFeatureMapType()
							&& !genFeature.isMapType()) {
						String rawListItemType = genFeature.getRawListItemType();
						int index = rawListItemType.indexOf('[');
						String head = rawListItemType;
						String tail = "";
						if (index != -1) {
							head = rawListItemType.substring(0, index);
							tail = rawListItemType.substring(index);
						}
						stringBuffer.append(TEXT_85);
						stringBuffer.append(genFeature.getGetArrayAccessor());
						stringBuffer.append(TEXT_86);
						stringBuffer.append(genFeature.getFormattedName());
						stringBuffer.append(TEXT_87);
						stringBuffer.append(genFeature.getGetArrayAccessor());
						stringBuffer.append(TEXT_88);
						if (genFeature.getQualifiedListItemType(genClass).contains("<")) {
							stringBuffer.append(TEXT_89);
						}
						stringBuffer.append(TEXT_90);
						stringBuffer.append(rawListItemType);
						stringBuffer.append(TEXT_91);
						stringBuffer.append(genFeature.getUpperName());
						stringBuffer.append(TEXT_92);
						stringBuffer.append(head);
						stringBuffer.append(TEXT_93);
						stringBuffer.append(tail);
						stringBuffer.append(TEXT_94);
					}
				} else {
					if (genFeature.hasEDefault() && (!genFeature.isVolatile() || !genModel.isReflectiveDelegation()
							&& (!genFeature.hasDelegateFeature() || !genFeature.isUnsettable()))) {
						String staticDefaultValue = genFeature.getStaticDefaultValue();
						stringBuffer.append(TEXT_95);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_96);
						stringBuffer.append(genFeature.getFormattedName());
						stringBuffer.append(TEXT_97);
						stringBuffer.append(genFeature.getFeatureKind());
						stringBuffer.append(TEXT_98);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_99);
						if (genModel.useGenerics() && genFeature.isListDataType() && genFeature.isSetDefaultValue()) {
							stringBuffer.append(TEXT_100);
						}
						stringBuffer.append(TEXT_101);
						stringBuffer.append(genFeature.getImportedType(genClass));
						stringBuffer.append(TEXT_102);
						stringBuffer.append(genFeature.getEDefault());
						if ("".equals(staticDefaultValue)) {
							stringBuffer.append(TEXT_103);
							stringBuffer.append(genFeature.getEcoreFeature().getDefaultValueLiteral());
							stringBuffer.append(TEXT_104);
						} else {
							stringBuffer.append(TEXT_105);
							stringBuffer.append(staticDefaultValue);
							stringBuffer.append(TEXT_106);
							stringBuffer.append(genModel.getNonNLS(staticDefaultValue));
						}
						stringBuffer.append(TEXT_107);
					}
					if (genClass.isField(genFeature)) {
						if (genClass.isFlag(genFeature)) {
							int flagIndex = genClass.getFlagIndex(genFeature);
							if (flagIndex > 31 && flagIndex % 32 == 0) {
								stringBuffer.append(TEXT_108);
								if (isGWT) {
									stringBuffer.append(TEXT_109);
									stringBuffer.append(
											genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
								}
								stringBuffer.append(TEXT_110);
								stringBuffer.append(genClass.getFlagsField(genFeature));
								stringBuffer.append(TEXT_111);
							}
							if (genFeature.isEnumType()) {
								stringBuffer.append(TEXT_112);
								stringBuffer.append(genFeature.getGetAccessor());
								stringBuffer.append(TEXT_113);
								stringBuffer.append(genFeature.getFormattedName());
								stringBuffer.append(TEXT_114);
								stringBuffer.append(genFeature.getFeatureKind());
								stringBuffer.append(TEXT_115);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_116);
								stringBuffer.append(flagIndex % 32);
								stringBuffer.append(TEXT_117);
								stringBuffer.append(genFeature.getGetAccessor());
								stringBuffer.append(TEXT_118);
								stringBuffer.append(genFeature.getFormattedName());
								stringBuffer.append(TEXT_119);
								stringBuffer.append(genFeature.getFeatureKind());
								stringBuffer.append(TEXT_120);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_121);
								if (isJDK50) {
									stringBuffer.append(genFeature.getEDefault());
									stringBuffer.append(TEXT_122);
								} else {
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_123);
									stringBuffer.append(genFeature.getEDefault());
									stringBuffer.append(TEXT_124);
								}
								stringBuffer.append(TEXT_125);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_126);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_127);
								stringBuffer.append(genFeature.getTypeGenClassifier().getFormattedName());
								stringBuffer.append(TEXT_128);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_129);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_130);
								if (isJDK50) {
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_131);
								} else {
									stringBuffer.append(TEXT_132);
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_133);
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_134);
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_135);
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_136);
								}
								stringBuffer.append(TEXT_137);
							}
							stringBuffer.append(TEXT_138);
							stringBuffer.append(genClass.getFlagSize(genFeature) > 1 ? "s" : "");
							stringBuffer.append(TEXT_139);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_140);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_141);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_142);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_143);
							stringBuffer.append(genFeature.getUpperName());
							stringBuffer.append(TEXT_144);
							stringBuffer.append(genClass.getFlagMask(genFeature));
							stringBuffer.append(TEXT_145);
							if (genFeature.isEnumType()) {
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_146);
							} else {
								stringBuffer.append(flagIndex % 32);
							}
							stringBuffer.append(TEXT_147);
						} else {
							stringBuffer.append(TEXT_148);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_149);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_150);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_151);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_152);
							if (isGWT) {
								stringBuffer.append(TEXT_153);
								stringBuffer.append(
										genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
							}
							stringBuffer.append(TEXT_154);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_155);
							stringBuffer.append(genFeature.getSafeName());
							if (genFeature.hasEDefault()) {
								stringBuffer.append(TEXT_156);
								stringBuffer.append(genFeature.getEDefault());
							}
							stringBuffer.append(TEXT_157);
						}
					}
				}
				if (genClass.isESetField(genFeature)) {
					if (genClass.isESetFlag(genFeature)) {
						int flagIndex = genClass.getESetFlagIndex(genFeature);
						if (flagIndex > 31 && flagIndex % 32 == 0) {
							stringBuffer.append(TEXT_158);
							if (isGWT) {
								stringBuffer.append(TEXT_159);
								stringBuffer.append(
										genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
							}
							stringBuffer.append(TEXT_160);
							stringBuffer.append(genClass.getESetFlagsField(genFeature));
							stringBuffer.append(TEXT_161);
						}
						stringBuffer.append(TEXT_162);
						stringBuffer.append(genFeature.getFormattedName());
						stringBuffer.append(TEXT_163);
						stringBuffer.append(genFeature.getFeatureKind());
						stringBuffer.append(TEXT_164);
						stringBuffer.append(genFeature.getUpperName());
						stringBuffer.append(TEXT_165);
						stringBuffer.append(flagIndex % 32);
						stringBuffer.append(TEXT_166);
					} else {
						stringBuffer.append(TEXT_167);
						stringBuffer.append(genFeature.getFormattedName());
						stringBuffer.append(TEXT_168);
						stringBuffer.append(genFeature.getFeatureKind());
						stringBuffer.append(TEXT_169);
						if (isGWT) {
							stringBuffer.append(TEXT_170);
							stringBuffer
									.append(genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
						}
						stringBuffer.append(TEXT_171);
						stringBuffer.append(genFeature.getUncapName());
						stringBuffer.append(TEXT_172);
					}
				}
				// Class/declaredFieldGenFeature.override.javajetinc
			}
		}
		if (isImplementation && genClass.hasOffsetCorrection() && !genClass.getImplementedGenFeatures().isEmpty()) {
			stringBuffer.append(TEXT_173);
			stringBuffer.append(genClass.getOffsetCorrectionField(null));
			stringBuffer.append(TEXT_174);
			stringBuffer.append(genClass.getQualifiedClassifierAccessor());
			stringBuffer.append(TEXT_175);
			stringBuffer.append(genClass.getImplementedGenFeatures().get(0).getQualifiedFeatureAccessor());
			stringBuffer.append(TEXT_176);
			stringBuffer.append(genClass.getQualifiedFeatureID(genClass.getImplementedGenFeatures().get(0)));
			stringBuffer.append(TEXT_177);
		}
		if (isImplementation && !genModel.isReflectiveDelegation()) {
			for (GenFeature genFeature : genClass.getImplementedGenFeatures()) {
				GenFeature reverseFeature = genFeature.getReverse();
				if (reverseFeature != null && reverseFeature.getGenClass().hasOffsetCorrection()) {
					stringBuffer.append(TEXT_178);
					stringBuffer.append(genClass.getOffsetCorrectionField(genFeature));
					stringBuffer.append(TEXT_179);
					stringBuffer.append(reverseFeature.getGenClass().getQualifiedClassifierAccessor());
					stringBuffer.append(TEXT_180);
					stringBuffer.append(reverseFeature.getQualifiedFeatureAccessor());
					stringBuffer.append(TEXT_181);
					stringBuffer.append(reverseFeature.getGenClass().getQualifiedFeatureID(reverseFeature));
					stringBuffer.append(TEXT_182);
				}
			}
		}
		if (genModel.isOperationReflection() && isImplementation && genClass.hasOffsetCorrection()
				&& !genClass.getImplementedGenOperations().isEmpty()) {
			stringBuffer.append(TEXT_183);
			stringBuffer.append(genClass.getQualifiedClassifierAccessor());
			stringBuffer.append(TEXT_184);
			stringBuffer.append(genClass.getImplementedGenOperations().get(0).getQualifiedOperationAccessor());
			stringBuffer.append(TEXT_185);
			stringBuffer.append(genClass.getQualifiedOperationID(genClass.getImplementedGenOperations().get(0)));
			stringBuffer.append(TEXT_186);
		}
		if (isImplementation) {
			stringBuffer.append(TEXT_187);
			if (genModel.isPublicConstructors()) {
				stringBuffer.append(TEXT_188);
			} else {
				stringBuffer.append(TEXT_189);
			}
			stringBuffer.append(TEXT_190);
			stringBuffer.append(genClass.getClassName());
			stringBuffer.append(TEXT_191);
			for (GenFeature genFeature : genClass.getFlagGenFeaturesWithDefault()) {
				stringBuffer.append(TEXT_192);
				stringBuffer.append(genClass.getFlagsField(genFeature));
				stringBuffer.append(TEXT_193);
				stringBuffer.append(genFeature.getUpperName());
				stringBuffer.append(TEXT_194);
				if (!genFeature.isBooleanType()) {
					stringBuffer.append(TEXT_195);
				}
				stringBuffer.append(TEXT_196);
			}

			stringBuffer.append(TEXT_197);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_198);
			}
			stringBuffer.append(TEXT_199);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EClass"));
			stringBuffer.append(TEXT_200);
			stringBuffer.append(genClass.getQualifiedClassifierAccessor());
			stringBuffer.append(TEXT_201);
		}
		if (isImplementation
				&& (genModel.getFeatureDelegation() == GenDelegationKind.REFLECTIVE_LITERAL
						|| genModel.isDynamicDelegation())
				&& (genClass.getClassExtendsGenClass() == null || (genClass.getClassExtendsGenClass().getGenModel()
						.getFeatureDelegation() != GenDelegationKind.REFLECTIVE_LITERAL
						&& !genClass.getClassExtendsGenClass().getGenModel().isDynamicDelegation()))) {
			stringBuffer.append(TEXT_202);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_203);
			}
			stringBuffer.append(TEXT_204);
			stringBuffer.append(genClass.getClassExtendsGenClass() == null ? 0
					: genClass.getClassExtendsGenClass().getAllGenFeatures().size());
			stringBuffer.append(TEXT_205);
		}
		// Class/reflectiveDelegation.override.javajetinc
		new Runnable() {
			@Override
			public void run() {
				for (GenFeature genFeature : (isImplementation ? genClass.getImplementedGenFeatures()
						: genClass.getDeclaredGenFeatures())) {
					if (genModel.isArrayAccessors() && genFeature.isListType() && !genFeature.isFeatureMapType()
							&& !genFeature.isMapType()) {
						stringBuffer.append(TEXT_206);
						if (!isImplementation) {
							stringBuffer.append(TEXT_207);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_208);
							stringBuffer.append(genFeature.getGetArrayAccessor());
							stringBuffer.append(TEXT_209);
						} else {
							stringBuffer.append(TEXT_210);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_211);
							stringBuffer.append(genFeature.getGetArrayAccessor());
							stringBuffer.append(TEXT_212);
							if (genFeature.isVolatile()) {
								stringBuffer.append(TEXT_213);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicEList"));
								stringBuffer.append(genFeature.getListTemplateArguments(genClass));
								stringBuffer.append(TEXT_214);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicEList"));
								stringBuffer.append(genFeature.getListTemplateArguments(genClass));
								stringBuffer.append(TEXT_215);
								stringBuffer.append(genFeature.getGetAccessor());
								stringBuffer.append(TEXT_216);
								if (genModel.useGenerics() && !genFeature.getListItemType(genClass).contains("<")
										&& !genFeature.getListItemType(null)
												.equals(genFeature.getListItemType(genClass))) {
									stringBuffer.append(TEXT_217);
									stringBuffer.append(genFeature.getListItemType(genClass));
									stringBuffer.append(TEXT_218);
								}
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_219);
							} else {
								stringBuffer.append(TEXT_220);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_221);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_222);
								if (genModel.useGenerics() && !genFeature.getListItemType(genClass).contains("<")
										&& !genFeature.getListItemType(null)
												.equals(genFeature.getListItemType(genClass))) {
									stringBuffer.append(TEXT_223);
									stringBuffer.append(genFeature.getListItemType(genClass));
									stringBuffer.append(TEXT_224);
								}
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_225);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicEList"));
								stringBuffer.append(genFeature.getListTemplateArguments(genClass));
								stringBuffer.append(TEXT_226);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicEList"));
								stringBuffer.append(genFeature.getListTemplateArguments(genClass));
								stringBuffer.append(TEXT_227);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_228);
							}
							stringBuffer.append(TEXT_229);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_230);
						}
						stringBuffer.append(TEXT_231);
						if (!isImplementation) {
							stringBuffer.append(TEXT_232);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_233);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_234);
						} else {
							stringBuffer.append(TEXT_235);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_236);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_237);
							if (!genModel.useGenerics()) {
								stringBuffer.append(TEXT_238);
								stringBuffer.append(genFeature.getListItemType(genClass));
								stringBuffer.append(TEXT_239);
							}
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_240);
						}
						stringBuffer.append(TEXT_241);
						if (!isImplementation) {
							stringBuffer.append(TEXT_242);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_243);
						} else {
							stringBuffer.append(TEXT_244);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_245);
							if (genFeature.isVolatile()) {
								stringBuffer.append(TEXT_246);
								stringBuffer.append(genFeature.getGetAccessor());
								stringBuffer.append(TEXT_247);
							} else {
								stringBuffer.append(TEXT_248);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_249);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_250);
							}
							stringBuffer.append(TEXT_251);
						}
						stringBuffer.append(TEXT_252);
						if (!isImplementation) {
							stringBuffer.append(TEXT_253);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_254);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_255);
							stringBuffer.append(genFeature.getCapName());
							stringBuffer.append(TEXT_256);
						} else {
							stringBuffer.append(TEXT_257);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_258);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_259);
							stringBuffer.append(genFeature.getCapName());
							stringBuffer.append(TEXT_260);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicEList"));
							stringBuffer.append(genFeature.getListTemplateArguments(genClass));
							stringBuffer.append(TEXT_261);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_262);
							stringBuffer.append(genFeature.getCapName());
							stringBuffer.append(TEXT_263);
							stringBuffer.append(genFeature.getCapName());
							stringBuffer.append(TEXT_264);
						}
						stringBuffer.append(TEXT_265);
						if (!isImplementation) {
							stringBuffer.append(TEXT_266);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_267);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_268);
						} else {
							stringBuffer.append(TEXT_269);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_270);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_271);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_272);
						}
					}
					if (genFeature.isGet() && (isImplementation || !genFeature.isSuppressedGetVisibility())) {
						if (isInterface) {
							stringBuffer.append(TEXT_273);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_274);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_275);
							if (genFeature.isListType()) {
								if (genFeature.isMapType()) {
									GenFeature keyFeature = genFeature.getMapEntryTypeGenClass()
											.getMapEntryKeyFeature();
									GenFeature valueFeature = genFeature.getMapEntryTypeGenClass()
											.getMapEntryValueFeature();
									stringBuffer.append(TEXT_276);
									if (keyFeature.isListType()) {
										stringBuffer.append(TEXT_277);
										stringBuffer.append(keyFeature.getQualifiedListItemType(genClass));
										stringBuffer.append(TEXT_278);
									} else {
										stringBuffer.append(TEXT_279);
										stringBuffer.append(keyFeature.getType(genClass));
										stringBuffer.append(TEXT_280);
									}
									stringBuffer.append(TEXT_281);
									if (valueFeature.isListType()) {
										stringBuffer.append(TEXT_282);
										stringBuffer.append(valueFeature.getQualifiedListItemType(genClass));
										stringBuffer.append(TEXT_283);
									} else {
										stringBuffer.append(TEXT_284);
										stringBuffer.append(valueFeature.getType(genClass));
										stringBuffer.append(TEXT_285);
									}
									stringBuffer.append(TEXT_286);
								} else if (!genFeature.isWrappedFeatureMapType()
										&& !(genModel.isSuppressEMFMetaData() && "org.eclipse.emf.ecore.EObject"
												.equals(genFeature.getQualifiedListItemType(genClass)))) {
									String typeName = genFeature.getQualifiedListItemType(genClass);
									String head = typeName;
									String tail = "";
									int index = typeName.indexOf('<');
									if (index == -1) {
										index = typeName.indexOf('[');
									}
									if (index != -1) {
										head = typeName.substring(0, index);
										tail = typeName.substring(index).replaceAll("<", "&lt;");
									}

									stringBuffer.append(TEXT_287);
									stringBuffer.append(head);
									stringBuffer.append(TEXT_288);
									stringBuffer.append(tail);
									stringBuffer.append(TEXT_289);
								}
							} else if (genFeature.isSetDefaultValue()) {
								stringBuffer.append(TEXT_290);
								stringBuffer.append(genFeature.getDefaultValue());
								stringBuffer.append(TEXT_291);
							}
							if (genFeature.getTypeGenEnum() != null) {
								stringBuffer.append(TEXT_292);
								stringBuffer.append(genFeature.getTypeGenEnum().getQualifiedName());
								stringBuffer.append(TEXT_293);
							}
							if (genFeature.isBidirectional() && !genFeature.getReverse().getGenClass().isMapEntry()) {
								GenFeature reverseGenFeature = genFeature.getReverse();
								if (!reverseGenFeature.isSuppressedGetVisibility()) {
									stringBuffer.append(TEXT_294);
									stringBuffer.append(reverseGenFeature.getGenClass().getQualifiedInterfaceName());
									stringBuffer.append(TEXT_295);
									stringBuffer.append(reverseGenFeature.getGetAccessor());
									stringBuffer.append(TEXT_296);
									stringBuffer.append(reverseGenFeature.getFormattedName());
									stringBuffer.append(TEXT_297);
								}
							}
							stringBuffer.append(TEXT_298);
							if (!genFeature.hasDocumentation()) {
								stringBuffer.append(TEXT_299);
								stringBuffer.append(genFeature.getFormattedName());
								stringBuffer.append(TEXT_300);
								stringBuffer.append(genFeature.getFeatureKind());
								stringBuffer.append(TEXT_301);
							}
							stringBuffer.append(TEXT_302);
							if (genFeature.hasDocumentation()) {
								stringBuffer.append(TEXT_303);
								stringBuffer.append(genFeature.getDocumentation(genModel.getIndentation(stringBuffer)));
								stringBuffer.append(TEXT_304);
							}
							stringBuffer.append(TEXT_305);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_306);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_307);
							if (genFeature.getTypeGenEnum() != null) {
								stringBuffer.append(TEXT_308);
								stringBuffer.append(genFeature.getTypeGenEnum().getQualifiedName());
							}
							if (genFeature.isUnsettable()) {
								if (!genFeature.isSuppressedIsSetVisibility()) {
									stringBuffer.append(TEXT_309);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_310);
								}
								if (genFeature.isChangeable() && !genFeature.isSuppressedUnsetVisibility()) {
									stringBuffer.append(TEXT_311);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_312);
								}
							}
							if (genFeature.isChangeable() && !genFeature.isListType()
									&& !genFeature.isSuppressedSetVisibility()) {
								stringBuffer.append(TEXT_313);
								stringBuffer.append(genFeature.getAccessorName());
								stringBuffer.append(TEXT_314);
								stringBuffer.append(genFeature.getRawImportedBoundType());
								stringBuffer.append(TEXT_315);
							}
							if (!genModel.isSuppressEMFMetaData()) {
								stringBuffer.append(TEXT_316);
								stringBuffer.append(genPackage.getQualifiedPackageInterfaceName());
								stringBuffer.append(TEXT_317);
								stringBuffer.append(genFeature.getFeatureAccessorName());
								stringBuffer.append(TEXT_318);
							}
							if (genFeature.isBidirectional() && !genFeature.getReverse().getGenClass().isMapEntry()) {
								GenFeature reverseGenFeature = genFeature.getReverse();
								if (!reverseGenFeature.isSuppressedGetVisibility()) {
									stringBuffer.append(TEXT_319);
									stringBuffer.append(reverseGenFeature.getGenClass().getQualifiedInterfaceName());
									stringBuffer.append(TEXT_320);
									stringBuffer.append(reverseGenFeature.getGetAccessor());
								}
							}
							if (!genModel.isSuppressEMFModelTags()) {
								boolean first = true;
								for (StringTokenizer stringTokenizer = new StringTokenizer(genFeature.getModelInfo(),
										"\n\r"); stringTokenizer.hasMoreTokens();) {
									String modelInfo = stringTokenizer.nextToken();
									if (first) {
										first = false;
										stringBuffer.append(TEXT_321);
										stringBuffer.append(modelInfo);
									} else {
										stringBuffer.append(TEXT_322);
										stringBuffer.append(modelInfo);
									}
								}
								if (first) {
									stringBuffer.append(TEXT_323);
								}
							}
							stringBuffer.append(TEXT_324);
							// Class/getGenFeature.javadoc.override.javajetinc
						} else {
							stringBuffer.append(TEXT_325);
							if (isJDK50) { // Class/getGenFeature.annotations.insert.javajetinc
							}
						}
						if (!isImplementation) {
							stringBuffer.append(TEXT_326);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_327);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_328);
						} else {
							if (genModel.useGenerics() && ((genFeature.isContainer() || genFeature.isResolveProxies())
									&& !genFeature.isListType()
									&& !(genModel.isReflectiveDelegation() && genModel.isDynamicDelegation())
									&& genFeature.isUncheckedCast(genClass)
									|| genFeature.isListType() && !genFeature.isFeatureMapType()
											&& (genModel.isReflectiveDelegation() || genModel.isVirtualDelegation()
													|| genModel.isDynamicDelegation())
									|| genFeature.isListDataType() && genFeature.hasDelegateFeature()
									|| genFeature.isListType() && genFeature.hasSettingDelegate())) {
								stringBuffer.append(TEXT_329);
							}
							stringBuffer.append(TEXT_330);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_331);
							stringBuffer.append(genFeature.getGetAccessor());
							if (genClass.hasCollidingGetAccessorOperation(genFeature)) {
								stringBuffer.append(TEXT_332);
							}
							stringBuffer.append(TEXT_333);

							if (genModel.isDynamicDelegation()) {
								stringBuffer.append(TEXT_334);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_335);
								}
								stringBuffer.append(TEXT_336);
								stringBuffer.append(genFeature.getObjectType(genClass));
								stringBuffer.append(TEXT_337);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(TEXT_338);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_339);
								stringBuffer.append(!genFeature.isEffectiveSuppressEMFTypes());
								stringBuffer.append(TEXT_340);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_341);
									stringBuffer.append(genFeature.getPrimitiveValueFunction());
									stringBuffer.append(TEXT_342);
								}
								stringBuffer.append(TEXT_343);
							} else if (genModel.isReflectiveDelegation()) {
								stringBuffer.append(TEXT_344);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_345);
								}
								stringBuffer.append(TEXT_346);
								stringBuffer.append(genFeature.getObjectType(genClass));
								stringBuffer.append(TEXT_347);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_348);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_349);
									stringBuffer.append(genFeature.getPrimitiveValueFunction());
									stringBuffer.append(TEXT_350);
								}
								stringBuffer.append(TEXT_351);
							} else if (genFeature.hasSettingDelegate()) {
								stringBuffer.append(TEXT_352);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_353);
								}
								stringBuffer.append(TEXT_354);
								stringBuffer.append(genFeature.getObjectType(genClass));
								stringBuffer.append(TEXT_355);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_356);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_357);
									stringBuffer.append(genFeature.getPrimitiveValueFunction());
									stringBuffer.append(TEXT_358);
								}
								stringBuffer.append(TEXT_359);
							} else if (!genFeature.isVolatile()) {
								if (genFeature.isListType()) {
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_360);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_361);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_362);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_363);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_364);
									}
									stringBuffer.append(TEXT_365);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_366);
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_367);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_368);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_369);
										stringBuffer.append(genClass.getListConstructor(genFeature));
										stringBuffer.append(TEXT_370);
									} else {
										stringBuffer.append(TEXT_371);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_372);
										stringBuffer.append(genClass.getListConstructor(genFeature));
										stringBuffer.append(TEXT_373);
									}
									stringBuffer.append(TEXT_374);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer
											.append(genFeature.isMapType() && genFeature.isEffectiveSuppressEMFTypes()
													? ".map()"
													: "");
									stringBuffer.append(TEXT_375);
								} else if (genFeature.isContainer()) {
									stringBuffer.append(TEXT_376);
									stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
									stringBuffer.append(positiveOffsetCorrection);
									stringBuffer.append(TEXT_377);
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_378);
								} else {
									if (genFeature.isResolveProxies()) {
										if (genModel.isVirtualDelegation()) {
											stringBuffer.append(TEXT_379);
											stringBuffer.append(genFeature.getImportedType(genClass));
											stringBuffer.append(TEXT_380);
											stringBuffer.append(genFeature.getSafeName());
											stringBuffer.append(TEXT_381);
											stringBuffer.append(genFeature.getImportedType(genClass));
											stringBuffer.append(TEXT_382);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											if (genFeature.hasEDefault()) {
												stringBuffer.append(TEXT_383);
												stringBuffer.append(genFeature.getEDefault());
											}
											stringBuffer.append(TEXT_384);
										}
										stringBuffer.append(TEXT_385);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_386);
										stringBuffer.append(genFeature.getSafeNameAsEObject());
										stringBuffer.append(TEXT_387);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_388);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_389);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_390);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_391);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_392);
										stringBuffer.append(genFeature.getNonEObjectInternalTypeCast(genClass));
										stringBuffer.append(TEXT_393);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_394);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_395);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_396);
										if (genFeature.isEffectiveContains()) {
											stringBuffer.append(TEXT_397);
											stringBuffer.append(
													genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
											stringBuffer.append(TEXT_398);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_399);
											stringBuffer.append(
													genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
											stringBuffer.append(TEXT_400);
											stringBuffer.append(genFeature.getSafeName());
											stringBuffer.append(TEXT_401);
											if (!genFeature.isBidirectional()) {
												stringBuffer.append(TEXT_402);
												stringBuffer.append(genModel.getImportedName(
														"org.eclipse.emf.common.notify.NotificationChain"));
												stringBuffer.append(TEXT_403);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_404);
												stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
												stringBuffer.append(negativeOffsetCorrection);
												stringBuffer.append(TEXT_405);
											} else {
												GenFeature reverseFeature = genFeature.getReverse();
												GenClass targetClass = reverseFeature.getGenClass();
												String reverseOffsetCorrection = targetClass.hasOffsetCorrection()
														? " + " + genClass.getOffsetCorrectionField(genFeature)
														: "";
												stringBuffer.append(TEXT_406);
												stringBuffer.append(genModel.getImportedName(
														"org.eclipse.emf.common.notify.NotificationChain"));
												stringBuffer.append(TEXT_407);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_408);
												stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
												stringBuffer.append(reverseOffsetCorrection);
												stringBuffer.append(TEXT_409);
												stringBuffer.append(targetClass.getRawImportedInterfaceName());
												stringBuffer.append(TEXT_410);
											}
											stringBuffer.append(TEXT_411);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_412);
											if (!genFeature.isBidirectional()) {
												stringBuffer.append(TEXT_413);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_414);
												stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
												stringBuffer.append(negativeOffsetCorrection);
												stringBuffer.append(TEXT_415);
											} else {
												GenFeature reverseFeature = genFeature.getReverse();
												GenClass targetClass = reverseFeature.getGenClass();
												String reverseOffsetCorrection = targetClass.hasOffsetCorrection()
														? " + " + genClass.getOffsetCorrectionField(genFeature)
														: "";
												stringBuffer.append(TEXT_416);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_417);
												stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
												stringBuffer.append(reverseOffsetCorrection);
												stringBuffer.append(TEXT_418);
												stringBuffer.append(targetClass.getRawImportedInterfaceName());
												stringBuffer.append(TEXT_419);
											}
											stringBuffer.append(TEXT_420);
										} else if (genModel.isVirtualDelegation()) {
											stringBuffer.append(TEXT_421);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_422);
											stringBuffer.append(genFeature.getSafeName());
											stringBuffer.append(TEXT_423);
										}
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_424);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
											stringBuffer.append(TEXT_425);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.common.notify.Notification"));
											stringBuffer.append(TEXT_426);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_427);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_428);
											stringBuffer.append(genFeature.getSafeName());
											stringBuffer.append(TEXT_429);
										}
										stringBuffer.append(TEXT_430);
									}
									if (!genFeature.isResolveProxies() && genModel.isVirtualDelegation()
											&& !genFeature.isPrimitiveType()) {
										stringBuffer.append(TEXT_431);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_432);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										if (genFeature.hasEDefault()) {
											stringBuffer.append(TEXT_433);
											stringBuffer.append(genFeature.getEDefault());
										}
										stringBuffer.append(TEXT_434);
									} else if (genClass.isFlag(genFeature)) {
										if (genFeature.isBooleanType()) {
											stringBuffer.append(TEXT_435);
											stringBuffer.append(genClass.getFlagsField(genFeature));
											stringBuffer.append(TEXT_436);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_437);
										} else {
											stringBuffer.append(TEXT_438);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_439);
											stringBuffer.append(genClass.getFlagsField(genFeature));
											stringBuffer.append(TEXT_440);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_441);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_442);
										}
									} else {
										stringBuffer.append(TEXT_443);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_444);
									}
								}
							} else {// volatile
								if (genFeature.isResolveProxies() && !genFeature.isListType()) {
									stringBuffer.append(TEXT_445);
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_446);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_447);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_448);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_449);
									stringBuffer.append(genFeature.getSafeNameAsEObject());
									stringBuffer.append(TEXT_450);
									stringBuffer.append(genFeature.getNonEObjectInternalTypeCast(genClass));
									stringBuffer.append(TEXT_451);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
									stringBuffer.append(TEXT_452);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_453);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_454);
								} else if (genFeature.hasDelegateFeature()) {
									GenFeature delegateFeature = genFeature.getDelegateFeature();
									if (genFeature.isFeatureMapType()) {
										String featureMapEntryTemplateArgument = isJDK50 ? "<"
												+ genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap")
												+ ".Entry>" : "";
										if (delegateFeature.isWrappedFeatureMapType()) {
											stringBuffer.append(TEXT_455);
											stringBuffer
													.append(genFeature.getImportedEffectiveFeatureMapWrapperClass());
											stringBuffer.append(TEXT_456);
											stringBuffer.append(
													genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
											stringBuffer.append(TEXT_457);
											stringBuffer.append(
													genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
											stringBuffer.append(TEXT_458);
											stringBuffer.append(delegateFeature.getAccessorName());
											stringBuffer.append(TEXT_459);
											stringBuffer.append(featureMapEntryTemplateArgument);
											stringBuffer.append(TEXT_460);
											stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
											stringBuffer.append(TEXT_461);
										} else {
											stringBuffer.append(TEXT_462);
											stringBuffer.append(
													genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
											stringBuffer.append(TEXT_463);
											stringBuffer.append(delegateFeature.getAccessorName());
											stringBuffer.append(TEXT_464);
											stringBuffer.append(featureMapEntryTemplateArgument);
											stringBuffer.append(TEXT_465);
											stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
											stringBuffer.append(TEXT_466);
										}
									} else if (genFeature.isListType()) {
										if (delegateFeature.isWrappedFeatureMapType()) {
											stringBuffer.append(TEXT_467);
											stringBuffer.append(
													genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
											stringBuffer.append(TEXT_468);
											stringBuffer.append(delegateFeature.getAccessorName());
											stringBuffer.append(TEXT_469);
											stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
											stringBuffer.append(TEXT_470);
										} else {
											stringBuffer.append(TEXT_471);
											stringBuffer.append(delegateFeature.getAccessorName());
											stringBuffer.append(TEXT_472);
											stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
											stringBuffer.append(TEXT_473);
										}
									} else {
										if (delegateFeature.isWrappedFeatureMapType()) {
											stringBuffer.append(TEXT_474);
											if (!isJDK50 && genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_475);
											}
											if (genFeature.getTypeGenDataType() == null
													|| !genFeature.getTypeGenDataType().isObjectType()) {
												stringBuffer.append(TEXT_476);
												stringBuffer.append(genFeature.getObjectType(genClass));
												stringBuffer.append(TEXT_477);
											}
											stringBuffer.append(TEXT_478);
											stringBuffer.append(
													genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
											stringBuffer.append(TEXT_479);
											stringBuffer.append(delegateFeature.getAccessorName());
											stringBuffer.append(TEXT_480);
											stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
											stringBuffer.append(TEXT_481);
											if (!isJDK50 && genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_482);
												stringBuffer.append(genFeature.getPrimitiveValueFunction());
												stringBuffer.append(TEXT_483);
											}
											stringBuffer.append(TEXT_484);
										} else {
											stringBuffer.append(TEXT_485);
											if (!isJDK50 && genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_486);
											}
											if (genFeature.getTypeGenDataType() == null
													|| !genFeature.getTypeGenDataType().isObjectType()) {
												stringBuffer.append(TEXT_487);
												stringBuffer.append(genFeature.getObjectType(genClass));
												stringBuffer.append(TEXT_488);
											}
											stringBuffer.append(TEXT_489);
											stringBuffer.append(delegateFeature.getAccessorName());
											stringBuffer.append(TEXT_490);
											stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
											stringBuffer.append(TEXT_491);
											if (!isJDK50 && genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_492);
												stringBuffer.append(genFeature.getPrimitiveValueFunction());
												stringBuffer.append(TEXT_493);
											}
											stringBuffer.append(TEXT_494);
										}
									}
								} else if (genClass.getGetAccessorOperation(genFeature) != null) {
									stringBuffer.append(TEXT_495);
									stringBuffer.append(genClass.getGetAccessorOperation(genFeature)
											.getBody(genModel.getIndentation(stringBuffer)));
								} else if (genFeature.hasGetterBody()) {
									stringBuffer.append(TEXT_496);
									stringBuffer
											.append(genFeature.getGetterBody(genModel.getIndentation(stringBuffer)));
								} else {
									stringBuffer.append(TEXT_497);
									stringBuffer.append(genFeature.getFormattedName());
									stringBuffer.append(TEXT_498);
									stringBuffer.append(genFeature.getFeatureKind());
									stringBuffer.append(TEXT_499);
									if (genFeature.isListType()) {
										stringBuffer.append(TEXT_500);
										if (genFeature.isMapType()) {
											stringBuffer.append(TEXT_501);
										} else if (genFeature.isFeatureMapType()) {
											stringBuffer.append(TEXT_502);
										} else {
											stringBuffer.append(TEXT_503);
										}
										stringBuffer.append(TEXT_504);
									}
									stringBuffer.append(TEXT_505);
									// Class/getGenFeature.todo.override.javajetinc
								}
							}
							stringBuffer.append(TEXT_506);
						}
						// Class/getGenFeature.override.javajetinc
					}
					if (isImplementation && !genModel.isReflectiveDelegation() && genFeature.isBasicGet()) {
						stringBuffer.append(TEXT_507);
						if (isJDK50) { // Class/basicGetGenFeature.annotations.insert.javajetinc
						}
						stringBuffer.append(TEXT_508);
						stringBuffer.append(genFeature.getImportedType(genClass));
						stringBuffer.append(TEXT_509);
						stringBuffer.append(genFeature.getAccessorName());
						stringBuffer.append(TEXT_510);
						if (genModel.isDynamicDelegation()) {
							stringBuffer.append(TEXT_511);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_512);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(TEXT_513);
							stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
							stringBuffer.append(TEXT_514);
							stringBuffer.append(!genFeature.isEffectiveSuppressEMFTypes());
							stringBuffer.append(TEXT_515);
						} else if (genFeature.hasSettingDelegate()) {
							stringBuffer.append(TEXT_516);
							if (!isJDK50 && genFeature.isPrimitiveType()) {
								stringBuffer.append(TEXT_517);
							}
							stringBuffer.append(TEXT_518);
							stringBuffer.append(genFeature.getObjectType(genClass));
							stringBuffer.append(TEXT_519);
							stringBuffer.append(genFeature.getUpperName());
							stringBuffer.append(TEXT_520);
							if (!isJDK50 && genFeature.isPrimitiveType()) {
								stringBuffer.append(TEXT_521);
								stringBuffer.append(genFeature.getPrimitiveValueFunction());
								stringBuffer.append(TEXT_522);
							}
							stringBuffer.append(TEXT_523);
						} else if (genFeature.isContainer()) {
							stringBuffer.append(TEXT_524);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_525);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_526);
						} else if (!genFeature.isVolatile()) {
							if (genModel.isVirtualDelegation()) {
								stringBuffer.append(TEXT_527);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_528);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(positiveOffsetCorrection);
								stringBuffer.append(TEXT_529);
							} else {
								stringBuffer.append(TEXT_530);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_531);
							}
						} else if (genFeature.hasDelegateFeature()) {
							GenFeature delegateFeature = genFeature.getDelegateFeature();
							if (delegateFeature.isWrappedFeatureMapType()) {
								stringBuffer.append(TEXT_532);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_533);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
								stringBuffer.append(TEXT_534);
								stringBuffer.append(delegateFeature.getAccessorName());
								stringBuffer.append(TEXT_535);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_536);
							} else {
								stringBuffer.append(TEXT_537);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_538);
								stringBuffer.append(delegateFeature.getAccessorName());
								stringBuffer.append(TEXT_539);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_540);
							}
						} else {
							stringBuffer.append(TEXT_541);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_542);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_543);
							// Class/basicGetGenFeature.todo.override.javajetinc
						}
						stringBuffer.append(TEXT_544);
						// Class/basicGetGenFeature.override.javajetinc
					}
					if (isImplementation && !genModel.isReflectiveDelegation() && genFeature.isBasicSet()) {
						stringBuffer.append(TEXT_545);
						if (isJDK50) { // Class/basicSetGenFeature.annotations.insert.javajetinc
						}
						stringBuffer.append(TEXT_546);
						stringBuffer
								.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
						stringBuffer.append(TEXT_547);
						stringBuffer.append(genFeature.getAccessorName());
						stringBuffer.append(TEXT_548);
						stringBuffer.append(genFeature.getImportedInternalType(genClass));
						stringBuffer.append(TEXT_549);
						stringBuffer.append(genFeature.getCapName());
						stringBuffer.append(TEXT_550);
						stringBuffer
								.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
						stringBuffer.append(TEXT_551);
						if (genFeature.isContainer()) {
							stringBuffer.append(TEXT_552);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
							stringBuffer.append(TEXT_553);
							stringBuffer.append(genFeature.getCapName());
							stringBuffer.append(TEXT_554);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_555);
							stringBuffer.append(TEXT_556);
						} else if (genModel.isDynamicDelegation()) {
							stringBuffer.append(TEXT_557);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
							stringBuffer.append(TEXT_558);
							stringBuffer.append(genFeature.getCapName());
							stringBuffer.append(TEXT_559);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_560);
							stringBuffer.append(TEXT_561);
						} else if (!genFeature.isVolatile()) {
							if (genModel.isVirtualDelegation()) {
								stringBuffer.append(TEXT_562);
								stringBuffer.append(genFeature.getCapName());
								stringBuffer.append(TEXT_563);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(positiveOffsetCorrection);
								stringBuffer.append(TEXT_564);
								stringBuffer.append(genFeature.getCapName());
								stringBuffer.append(TEXT_565);
							} else {
								stringBuffer.append(TEXT_566);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_567);
								stringBuffer.append(genFeature.getCapName());
								stringBuffer.append(TEXT_568);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_569);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_570);
								stringBuffer.append(genFeature.getCapName());
								stringBuffer.append(TEXT_571);
							}
							if (genFeature.isUnsettable()) {
								if (genModel.isVirtualDelegation()) {
									if (!genModel.isSuppressNotification()) {
										stringBuffer.append(TEXT_572);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_573);
									}
								} else if (genClass.isESetFlag(genFeature)) {
									stringBuffer.append(TEXT_574);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_575);
									stringBuffer.append(genClass.getESetFlagsField(genFeature));
									stringBuffer.append(TEXT_576);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_577);
									if (!genModel.isSuppressNotification()) {
										stringBuffer.append(TEXT_578);
										stringBuffer.append(genClass.getESetFlagsField(genFeature));
										stringBuffer.append(TEXT_579);
										stringBuffer.append(genFeature.getUpperName());
										stringBuffer.append(TEXT_580);
									}
								} else {
									if (!genModel.isSuppressNotification()) {
										stringBuffer.append(TEXT_581);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_582);
										stringBuffer.append(genFeature.getUncapName());
										stringBuffer.append(TEXT_583);
									}
									stringBuffer.append(TEXT_584);
									stringBuffer.append(genFeature.getUncapName());
									stringBuffer.append(TEXT_585);
								}
							}
							if (!genModel.isSuppressNotification()) {
								stringBuffer.append(TEXT_586);
								if (genFeature.isUnsettable()) {
									stringBuffer.append(TEXT_587);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
									stringBuffer.append(TEXT_588);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
									stringBuffer.append(TEXT_589);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.common.notify.Notification"));
									stringBuffer.append(TEXT_590);
									stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
									stringBuffer.append(positiveOffsetCorrection);
									stringBuffer.append(TEXT_591);
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_592);
										stringBuffer.append(genFeature.getCapName());
									} else {
										stringBuffer.append(TEXT_593);
										stringBuffer.append(genFeature.getCapName());
									}
									stringBuffer.append(TEXT_594);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_595);
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_596);
									} else {
										stringBuffer.append(TEXT_597);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_598);
									}
									stringBuffer.append(TEXT_599);
								} else {
									stringBuffer.append(TEXT_600);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
									stringBuffer.append(TEXT_601);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
									stringBuffer.append(TEXT_602);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.common.notify.Notification"));
									stringBuffer.append(TEXT_603);
									stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
									stringBuffer.append(positiveOffsetCorrection);
									stringBuffer.append(TEXT_604);
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_605);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_606);
										stringBuffer.append(genFeature.getCapName());
									} else {
										stringBuffer.append(TEXT_607);
										stringBuffer.append(genFeature.getCapName());
									}
									stringBuffer.append(TEXT_608);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_609);
								}
								stringBuffer.append(TEXT_610);
							}
							stringBuffer.append(TEXT_611);
						} else if (genFeature.hasDelegateFeature()) {
							GenFeature delegateFeature = genFeature.getDelegateFeature();
							if (delegateFeature.isWrappedFeatureMapType()) {
								stringBuffer.append(TEXT_612);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
								stringBuffer.append(TEXT_613);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
								stringBuffer.append(TEXT_614);
								stringBuffer.append(delegateFeature.getAccessorName());
								stringBuffer.append(TEXT_615);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_616);
								stringBuffer.append(genFeature.getCapName());
								stringBuffer.append(TEXT_617);
							} else {
								stringBuffer.append(TEXT_618);
								stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
								stringBuffer.append(TEXT_619);
								stringBuffer.append(delegateFeature.getAccessorName());
								stringBuffer.append(TEXT_620);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_621);
								stringBuffer.append(genFeature.getCapName());
								stringBuffer.append(TEXT_622);
							}
						} else {
							stringBuffer.append(TEXT_623);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_624);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_625);
							// Class/basicSetGenFeature.todo.override.javajetinc
						}
						stringBuffer.append(TEXT_626);
						// Class/basicSetGenFeature.override.javajetinc
					}
					if (genFeature.isSet() && (isImplementation || !genFeature.isSuppressedSetVisibility())) {
						if (isInterface) {
							stringBuffer.append(TEXT_627);
							stringBuffer.append(genClass.getQualifiedInterfaceName());
							stringBuffer.append(TEXT_628);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_629);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_630);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_631);
							stringBuffer.append(TEXT_632);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_633);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_634);
							if (genFeature.isEnumType()) {
								stringBuffer.append(TEXT_635);
								stringBuffer.append(genFeature.getTypeGenEnum().getQualifiedName());
							}
							if (genFeature.isUnsettable()) {
								if (!genFeature.isSuppressedIsSetVisibility()) {
									stringBuffer.append(TEXT_636);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_637);
								}
								if (!genFeature.isSuppressedUnsetVisibility()) {
									stringBuffer.append(TEXT_638);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_639);
								}
							}
							stringBuffer.append(TEXT_640);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_641);
							// Class/setGenFeature.javadoc.override.javajetinc
						} else {
							stringBuffer.append(TEXT_642);
							if (isJDK50) { // Class/setGenFeature.annotations.insert.javajetinc
							}
						}
						if (!isImplementation) {
							stringBuffer.append(TEXT_643);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_644);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_645);
						} else {
							GenOperation setAccessorOperation = genClass.getSetAccessorOperation(genFeature);
							stringBuffer.append(TEXT_646);
							stringBuffer.append(genFeature.getAccessorName());
							if (genClass.hasCollidingSetAccessorOperation(genFeature)) {
								stringBuffer.append(TEXT_647);
							}
							stringBuffer.append(TEXT_648);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_649);
							stringBuffer.append(setAccessorOperation == null ? "new" + genFeature.getCapName()
									: setAccessorOperation.getGenParameters().get(0).getName());
							stringBuffer.append(TEXT_650);

							if (genModel.isDynamicDelegation()) {
								stringBuffer.append(TEXT_651);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(TEXT_652);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_653);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_654);
									stringBuffer.append(genFeature.getObjectType(genClass));
									stringBuffer.append(TEXT_655);
								}
								stringBuffer.append(TEXT_656);
								stringBuffer.append(genFeature.getCapName());
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_657);
								}
								stringBuffer.append(TEXT_658);
							} else if (genModel.isReflectiveDelegation()) {
								stringBuffer.append(TEXT_659);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_660);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_661);
									stringBuffer.append(genFeature.getObjectType(genClass));
									stringBuffer.append(TEXT_662);
								}
								stringBuffer.append(TEXT_663);
								stringBuffer.append(genFeature.getCapName());
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_664);
								}
								stringBuffer.append(TEXT_665);
							} else if (genFeature.hasSettingDelegate()) {
								stringBuffer.append(TEXT_666);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_667);
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_668);
									stringBuffer.append(genFeature.getObjectType(genClass));
									stringBuffer.append(TEXT_669);
								}
								stringBuffer.append(TEXT_670);
								stringBuffer.append(genFeature.getCapName());
								if (!isJDK50 && genFeature.isPrimitiveType()) {
									stringBuffer.append(TEXT_671);
								}
								stringBuffer.append(TEXT_672);
							} else if (!genFeature.isVolatile()) {
								if (genFeature.isContainer()) {
									GenFeature reverseFeature = genFeature.getReverse();
									GenClass targetClass = reverseFeature.getGenClass();
									String reverseOffsetCorrection = targetClass.hasOffsetCorrection()
											? " + " + genClass.getOffsetCorrectionField(genFeature)
											: "";
									stringBuffer.append(TEXT_673);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_674);
									stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
									stringBuffer.append(positiveOffsetCorrection);
									stringBuffer.append(TEXT_675);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_676);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.EcoreUtil"));
									stringBuffer.append(TEXT_677);
									stringBuffer.append(genFeature.getEObjectCast());
									stringBuffer.append(TEXT_678);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_679);
									stringBuffer.append(genModel.getImportedName("java.lang.IllegalArgumentException"));
									stringBuffer.append(TEXT_680);
									stringBuffer.append(genModel.getNonNLS());
									stringBuffer.append(TEXT_681);
									stringBuffer.append(genModel
											.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
									stringBuffer.append(TEXT_682);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_683);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
									stringBuffer.append(TEXT_684);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_685);
									stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
									stringBuffer.append(reverseOffsetCorrection);
									stringBuffer.append(TEXT_686);
									stringBuffer.append(targetClass.getRawImportedInterfaceName());
									stringBuffer.append(TEXT_687);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_688);
									stringBuffer.append(genFeature.getInternalTypeCast());
									stringBuffer.append(TEXT_689);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_690);
									if (!genModel.isSuppressNotification()) {
										stringBuffer.append(TEXT_691);
										stringBuffer.append(genModel
												.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
										stringBuffer.append(TEXT_692);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.common.notify.Notification"));
										stringBuffer.append(TEXT_693);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_694);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_695);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_696);
									}
								} else if (genFeature.isBidirectional() || genFeature.isEffectiveContains()) {
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_697);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_698);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_699);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_700);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_701);
									}
									stringBuffer.append(TEXT_702);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_703);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_704);
									stringBuffer.append(genModel
											.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
									stringBuffer.append(TEXT_705);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_706);
									if (!genFeature.isBidirectional()) {
										stringBuffer.append(TEXT_707);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_708);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_709);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(negativeOffsetCorrection);
										stringBuffer.append(TEXT_710);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_711);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_712);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_713);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(negativeOffsetCorrection);
										stringBuffer.append(TEXT_714);
									} else {
										GenFeature reverseFeature = genFeature.getReverse();
										GenClass targetClass = reverseFeature.getGenClass();
										String reverseOffsetCorrection = targetClass.hasOffsetCorrection()
												? " + " + genClass.getOffsetCorrectionField(genFeature)
												: "";
										stringBuffer.append(TEXT_715);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_716);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_717);
										stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
										stringBuffer.append(reverseOffsetCorrection);
										stringBuffer.append(TEXT_718);
										stringBuffer.append(targetClass.getRawImportedInterfaceName());
										stringBuffer.append(TEXT_719);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_720);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_721);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_722);
										stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
										stringBuffer.append(reverseOffsetCorrection);
										stringBuffer.append(TEXT_723);
										stringBuffer.append(targetClass.getRawImportedInterfaceName());
										stringBuffer.append(TEXT_724);
									}
									stringBuffer.append(TEXT_725);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_726);
									stringBuffer.append(genFeature.getInternalTypeCast());
									stringBuffer.append(TEXT_727);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_728);
									if (genFeature.isUnsettable()) {
										stringBuffer.append(TEXT_729);
										if (genModel.isVirtualDelegation()) {
											stringBuffer.append(TEXT_730);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_731);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_732);
										} else if (genClass.isESetFlag(genFeature)) {
											if (!genModel.isSuppressNotification()) {
												stringBuffer.append(TEXT_733);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_734);
												stringBuffer.append(genClass.getESetFlagsField(genFeature));
												stringBuffer.append(TEXT_735);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_736);
											}
											stringBuffer.append(TEXT_737);
											stringBuffer.append(genClass.getESetFlagsField(genFeature));
											stringBuffer.append(TEXT_738);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_739);
										} else {
											if (!genModel.isSuppressNotification()) {
												stringBuffer.append(TEXT_740);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_741);
												stringBuffer.append(genFeature.getUncapName());
												stringBuffer.append(TEXT_742);
											}
											stringBuffer.append(TEXT_743);
											stringBuffer.append(genFeature.getUncapName());
											stringBuffer.append(TEXT_744);
										}
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_745);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
											stringBuffer.append(TEXT_746);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.common.notify.Notification"));
											stringBuffer.append(TEXT_747);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_748);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_749);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_750);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_751);
										}
										stringBuffer.append(TEXT_752);
									} else {
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_753);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
											stringBuffer.append(TEXT_754);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.common.notify.Notification"));
											stringBuffer.append(TEXT_755);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_756);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_757);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_758);
										}
									}
								} else {
									if (genClass.isFlag(genFeature)) {
										if (!genModel.isSuppressNotification()) {
											if (genFeature.isBooleanType()) {
												stringBuffer.append(TEXT_759);
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_760);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_761);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_762);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_763);
											} else {
												stringBuffer.append(TEXT_764);
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_765);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_766);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_767);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_768);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_769);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_770);
											}
										}
										if (genFeature.isBooleanType()) {
											stringBuffer.append(TEXT_771);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_772);
											stringBuffer.append(genClass.getFlagsField(genFeature));
											stringBuffer.append(TEXT_773);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_774);
											stringBuffer.append(genClass.getFlagsField(genFeature));
											stringBuffer.append(TEXT_775);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_776);
										} else {
											stringBuffer.append(TEXT_777);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_778);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_779);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_780);
											stringBuffer.append(genClass.getFlagsField(genFeature));
											stringBuffer.append(TEXT_781);
											stringBuffer.append(genClass.getFlagsField(genFeature));
											stringBuffer.append(TEXT_782);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_783);
											if (isJDK50) {
												stringBuffer.append(TEXT_784);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_785);
											} else {
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_786);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_787);
											}
											stringBuffer.append(TEXT_788);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_789);
										}
									} else {
										if (!genModel.isVirtualDelegation() || genFeature.isPrimitiveType()) {
											if (!genModel.isSuppressNotification()) {
												stringBuffer.append(TEXT_790);
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_791);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_792);
												stringBuffer.append(genFeature.getSafeName());
												stringBuffer.append(TEXT_793);
											}
										}
										if (genFeature.isEnumType()) {
											if (genModel.isVirtualDelegation()) {
												stringBuffer.append(TEXT_794);
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_795);
												stringBuffer.append(genFeature.getSafeName());
												stringBuffer.append(TEXT_796);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_797);
												stringBuffer.append(genFeature.getEDefault());
												stringBuffer.append(TEXT_798);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_799);
											} else {
												stringBuffer.append(TEXT_800);
												stringBuffer.append(genFeature.getSafeName());
												stringBuffer.append(TEXT_801);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_802);
												stringBuffer.append(genFeature.getEDefault());
												stringBuffer.append(TEXT_803);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_804);
											}
										} else {
											if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_805);
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_806);
												stringBuffer.append(genFeature.getSafeName());
												stringBuffer.append(TEXT_807);
												stringBuffer.append(genFeature.getInternalTypeCast());
												stringBuffer.append(TEXT_808);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_809);
											} else {
												stringBuffer.append(TEXT_810);
												stringBuffer.append(genFeature.getSafeName());
												stringBuffer.append(TEXT_811);
												stringBuffer.append(genFeature.getInternalTypeCast());
												stringBuffer.append(TEXT_812);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_813);
											}
										}
										if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
											stringBuffer.append(TEXT_814);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_815);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_816);
											stringBuffer.append(genFeature.getSafeName());
											stringBuffer.append(TEXT_817);
										}
									}
									if (genFeature.isUnsettable()) {
										if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
											stringBuffer.append(TEXT_818);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_819);
										} else if (genClass.isESetFlag(genFeature)) {
											if (!genModel.isSuppressNotification()) {
												stringBuffer.append(TEXT_820);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_821);
												stringBuffer.append(genClass.getESetFlagsField(genFeature));
												stringBuffer.append(TEXT_822);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_823);
											}
											stringBuffer.append(TEXT_824);
											stringBuffer.append(genClass.getESetFlagsField(genFeature));
											stringBuffer.append(TEXT_825);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_826);
										} else {
											if (!genModel.isSuppressNotification()) {
												stringBuffer.append(TEXT_827);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_828);
												stringBuffer.append(genFeature.getUncapName());
												stringBuffer.append(TEXT_829);
											}
											stringBuffer.append(TEXT_830);
											stringBuffer.append(genFeature.getUncapName());
											stringBuffer.append(TEXT_831);
										}
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_832);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
											stringBuffer.append(TEXT_833);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.common.notify.Notification"));
											stringBuffer.append(TEXT_834);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_835);
											if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_836);
												stringBuffer.append(genFeature.getEDefault());
												stringBuffer.append(TEXT_837);
												stringBuffer.append(genFeature.getCapName());
											} else {
												stringBuffer.append(TEXT_838);
												stringBuffer.append(genFeature.getCapName());
											}
											stringBuffer.append(TEXT_839);
											if (genClass.isFlag(genFeature)) {
												stringBuffer.append(TEXT_840);
												stringBuffer.append(genFeature.getCapName());
											} else {
												stringBuffer.append(genFeature.getSafeName());
											}
											stringBuffer.append(TEXT_841);
											if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_842);
											} else {
												stringBuffer.append(TEXT_843);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_844);
											}
											stringBuffer.append(TEXT_845);
										}
									} else {
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_846);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
											stringBuffer.append(TEXT_847);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.common.notify.Notification"));
											stringBuffer.append(TEXT_848);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_849);
											if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_850);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_851);
												stringBuffer.append(genFeature.getEDefault());
												stringBuffer.append(TEXT_852);
												stringBuffer.append(genFeature.getCapName());
											} else {
												stringBuffer.append(TEXT_853);
												stringBuffer.append(genFeature.getCapName());
											}
											stringBuffer.append(TEXT_854);
											if (genClass.isFlag(genFeature)) {
												stringBuffer.append(TEXT_855);
												stringBuffer.append(genFeature.getCapName());
											} else {
												stringBuffer.append(genFeature.getSafeName());
											}
											stringBuffer.append(TEXT_856);
										}
									}
								}
							} else if (genFeature.hasDelegateFeature()) {
								GenFeature delegateFeature = genFeature.getDelegateFeature();
								if (delegateFeature.isWrappedFeatureMapType()) {
									stringBuffer.append(TEXT_857);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_858);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_859);
									stringBuffer.append(delegateFeature.getAccessorName());
									stringBuffer.append(TEXT_860);
									stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
									stringBuffer.append(TEXT_861);
									if (!isJDK50 && genFeature.isPrimitiveType()) {
										stringBuffer.append(TEXT_862);
										stringBuffer.append(genFeature.getObjectType(genClass));
										stringBuffer.append(TEXT_863);
									}
									stringBuffer.append(TEXT_864);
									stringBuffer.append(genFeature.getCapName());
									if (!isJDK50 && genFeature.isPrimitiveType()) {
										stringBuffer.append(TEXT_865);
									}
									stringBuffer.append(TEXT_866);
								} else {
									stringBuffer.append(TEXT_867);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_868);
									stringBuffer.append(delegateFeature.getAccessorName());
									stringBuffer.append(TEXT_869);
									stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
									stringBuffer.append(TEXT_870);
									if (!isJDK50 && genFeature.isPrimitiveType()) {
										stringBuffer.append(TEXT_871);
										stringBuffer.append(genFeature.getObjectType(genClass));
										stringBuffer.append(TEXT_872);
									}
									stringBuffer.append(TEXT_873);
									stringBuffer.append(genFeature.getCapName());
									if (!isJDK50 && genFeature.isPrimitiveType()) {
										stringBuffer.append(TEXT_874);
									}
									stringBuffer.append(TEXT_875);
								}
							} else if (setAccessorOperation != null) {
								stringBuffer.append(TEXT_876);
								stringBuffer
										.append(setAccessorOperation.getBody(genModel.getIndentation(stringBuffer)));
							} else {
								stringBuffer.append(TEXT_877);
								stringBuffer.append(genFeature.getFormattedName());
								stringBuffer.append(TEXT_878);
								stringBuffer.append(genFeature.getFeatureKind());
								stringBuffer.append(TEXT_879);
								// Class/setGenFeature.todo.override.javajetinc
							}
							stringBuffer.append(TEXT_880);
						}
						// Class/setGenFeature.override.javajetinc
					}
					if (isImplementation && !genModel.isReflectiveDelegation() && genFeature.isBasicUnset()) {
						stringBuffer.append(TEXT_881);
						if (isJDK50) { // Class/basicUnsetGenFeature.annotations.insert.javajetinc
						}
						stringBuffer.append(TEXT_882);
						stringBuffer
								.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
						stringBuffer.append(TEXT_883);
						stringBuffer.append(genFeature.getAccessorName());
						stringBuffer.append(TEXT_884);
						stringBuffer
								.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
						stringBuffer.append(TEXT_885);
						if (genModel.isDynamicDelegation()) {
							stringBuffer.append(TEXT_886);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
							stringBuffer.append(TEXT_887);
							if (genFeature.isResolveProxies()) {
								stringBuffer.append(TEXT_888);
								stringBuffer.append(genFeature.getAccessorName());
							} else {
								stringBuffer.append(genFeature.getGetAccessor());
							}
							stringBuffer.append(TEXT_889);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_890);
						} else if (!genFeature.isVolatile()) {
							if (genModel.isVirtualDelegation()) {
								if (!genModel.isSuppressNotification()) {
									stringBuffer.append(TEXT_891);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_892);
								}
								stringBuffer.append(TEXT_893);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(positiveOffsetCorrection);
								stringBuffer.append(TEXT_894);
							} else {
								if (!genModel.isSuppressNotification()) {
									stringBuffer.append(TEXT_895);
									stringBuffer.append(genFeature.getImportedType(genClass));
									stringBuffer.append(TEXT_896);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_897);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_898);
								}
								stringBuffer.append(TEXT_899);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_900);
							}
							if (genModel.isVirtualDelegation()) {
								if (!genModel.isSuppressNotification()) {
									stringBuffer.append(TEXT_901);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_902);
								}
							} else if (genClass.isESetFlag(genFeature)) {
								if (!genModel.isSuppressNotification()) {
									stringBuffer.append(TEXT_903);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_904);
									stringBuffer.append(genClass.getESetFlagsField(genFeature));
									stringBuffer.append(TEXT_905);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_906);
								}
								stringBuffer.append(TEXT_907);
								stringBuffer.append(genClass.getESetFlagsField(genFeature));
								stringBuffer.append(TEXT_908);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_909);
							} else {
								if (!genModel.isSuppressNotification()) {
									stringBuffer.append(TEXT_910);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_911);
									stringBuffer.append(genFeature.getUncapName());
									stringBuffer.append(TEXT_912);
								}
								stringBuffer.append(TEXT_913);
								stringBuffer.append(genFeature.getUncapName());
								stringBuffer.append(TEXT_914);
							}
							if (!genModel.isSuppressNotification()) {
								stringBuffer.append(TEXT_915);
								stringBuffer.append(
										genModel.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
								stringBuffer.append(TEXT_916);
								stringBuffer.append(
										genModel.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
								stringBuffer.append(TEXT_917);
								stringBuffer
										.append(genModel.getImportedName("org.eclipse.emf.common.notify.Notification"));
								stringBuffer.append(TEXT_918);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(positiveOffsetCorrection);
								stringBuffer.append(TEXT_919);
								if (genModel.isVirtualDelegation()) {
									stringBuffer.append(TEXT_920);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_921);
								} else {
									stringBuffer.append(TEXT_922);
									stringBuffer.append(genFeature.getCapName());
								}
								stringBuffer.append(TEXT_923);
								if (genModel.isVirtualDelegation()) {
									stringBuffer.append(TEXT_924);
								} else {
									stringBuffer.append(TEXT_925);
									stringBuffer.append(genFeature.getCapName());
									stringBuffer.append(TEXT_926);
								}
								stringBuffer.append(TEXT_927);
							}
						} else {
							stringBuffer.append(TEXT_928);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_929);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_930);
							// Class/basicUnsetGenFeature.todo.override.javajetinc
						}
						stringBuffer.append(TEXT_931);
						// Class.basicUnsetGenFeature.override.javajetinc
					}
					if (genFeature.isUnset() && (isImplementation || !genFeature.isSuppressedUnsetVisibility())) {
						if (isInterface) {
							stringBuffer.append(TEXT_932);
							stringBuffer.append(genClass.getQualifiedInterfaceName());
							stringBuffer.append(TEXT_933);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_934);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_935);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_936);
							stringBuffer.append(TEXT_937);
							if (!genFeature.isSuppressedIsSetVisibility()) {
								stringBuffer.append(TEXT_938);
								stringBuffer.append(genFeature.getAccessorName());
								stringBuffer.append(TEXT_939);
							}
							stringBuffer.append(TEXT_940);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_941);
							if (!genFeature.isListType() && !genFeature.isSuppressedSetVisibility()) {
								stringBuffer.append(TEXT_942);
								stringBuffer.append(genFeature.getAccessorName());
								stringBuffer.append(TEXT_943);
								stringBuffer.append(genFeature.getRawImportedBoundType());
								stringBuffer.append(TEXT_944);
							}
							stringBuffer.append(TEXT_945);
							// Class/unsetGenFeature.javadoc.override.javajetinc
						} else {
							stringBuffer.append(TEXT_946);
							if (isJDK50) { // Class/unsetGenFeature.annotations.insert.javajetinc
							}
						}
						if (!isImplementation) {
							stringBuffer.append(TEXT_947);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_948);
						} else {
							stringBuffer.append(TEXT_949);
							stringBuffer.append(genFeature.getAccessorName());
							if (genClass.hasCollidingUnsetAccessorOperation(genFeature)) {
								stringBuffer.append(TEXT_950);
							}
							stringBuffer.append(TEXT_951);
							if (genModel.isDynamicDelegation()) {
								stringBuffer.append(TEXT_952);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(TEXT_953);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_954);
							} else if (genModel.isReflectiveDelegation()) {
								stringBuffer.append(TEXT_955);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_956);
							} else if (genFeature.hasSettingDelegate()) {
								stringBuffer.append(TEXT_957);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_958);
							} else if (!genFeature.isVolatile()) {
								if (genFeature.isListType()) {
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_959);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_960);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_961);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_962);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_963);
									}
									stringBuffer.append(TEXT_964);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_965);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.ecore.util.InternalEList"));
									stringBuffer.append(TEXT_966);
									stringBuffer.append(singleWildcard);
									stringBuffer.append(TEXT_967);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_968);
								} else if (genFeature.isBidirectional() || genFeature.isEffectiveContains()) {
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_969);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_970);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_971);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_972);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_973);
									}
									stringBuffer.append(TEXT_974);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_975);
									stringBuffer.append(genModel
											.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
									stringBuffer.append(TEXT_976);
									if (!genFeature.isBidirectional()) {
										stringBuffer.append(TEXT_977);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_978);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_979);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(negativeOffsetCorrection);
										stringBuffer.append(TEXT_980);
									} else {
										GenFeature reverseFeature = genFeature.getReverse();
										GenClass targetClass = reverseFeature.getGenClass();
										String reverseOffsetCorrection = targetClass.hasOffsetCorrection()
												? " + " + genClass.getOffsetCorrectionField(genFeature)
												: "";
										stringBuffer.append(TEXT_981);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
										stringBuffer.append(TEXT_982);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_983);
										stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
										stringBuffer.append(reverseOffsetCorrection);
										stringBuffer.append(TEXT_984);
										stringBuffer.append(targetClass.getRawImportedInterfaceName());
										stringBuffer.append(TEXT_985);
									}
									stringBuffer.append(TEXT_986);
									stringBuffer.append(genFeature.getAccessorName());
									stringBuffer.append(TEXT_987);
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_988);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_989);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_990);
									} else if (genClass.isESetFlag(genFeature)) {
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_991);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_992);
											stringBuffer.append(genClass.getESetFlagsField(genFeature));
											stringBuffer.append(TEXT_993);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_994);
										}
										stringBuffer.append(TEXT_995);
										stringBuffer.append(genClass.getESetFlagsField(genFeature));
										stringBuffer.append(TEXT_996);
										stringBuffer.append(genFeature.getUpperName());
										stringBuffer.append(TEXT_997);
									} else {
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_998);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_999);
											stringBuffer.append(genFeature.getUncapName());
											stringBuffer.append(TEXT_1000);
										}
										stringBuffer.append(TEXT_1001);
										stringBuffer.append(genFeature.getUncapName());
										stringBuffer.append(TEXT_1002);
									}
									if (!genModel.isSuppressNotification()) {
										stringBuffer.append(TEXT_1003);
										stringBuffer.append(genModel
												.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
										stringBuffer.append(TEXT_1004);
										stringBuffer.append(
												genModel.getImportedName("org.eclipse.emf.common.notify.Notification"));
										stringBuffer.append(TEXT_1005);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_1006);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_1007);
									}
									stringBuffer.append(TEXT_1008);
								} else {
									if (genClass.isFlag(genFeature)) {
										if (!genModel.isSuppressNotification()) {
											if (genFeature.isBooleanType()) {
												stringBuffer.append(TEXT_1009);
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_1010);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_1011);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_1012);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1013);
											} else {
												stringBuffer.append(TEXT_1014);
												stringBuffer.append(genFeature.getImportedType(genClass));
												stringBuffer.append(TEXT_1015);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_1016);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1017);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_1018);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1019);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1020);
											}
										}
									} else if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
										stringBuffer.append(TEXT_1021);
										stringBuffer.append(genFeature.getCapName());
										stringBuffer.append(TEXT_1022);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_1023);
									} else {
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_1024);
											stringBuffer.append(genFeature.getImportedType(genClass));
											stringBuffer.append(TEXT_1025);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_1026);
											stringBuffer.append(genFeature.getSafeName());
											stringBuffer.append(TEXT_1027);
										}
									}
									if (!genModel.isSuppressNotification()) {
										if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
											stringBuffer.append(TEXT_1028);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_1029);
										} else if (genClass.isESetFlag(genFeature)) {
											stringBuffer.append(TEXT_1030);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_1031);
											stringBuffer.append(genClass.getESetFlagsField(genFeature));
											stringBuffer.append(TEXT_1032);
											stringBuffer.append(genFeature.getUpperName());
											stringBuffer.append(TEXT_1033);
										} else {
											stringBuffer.append(TEXT_1034);
											stringBuffer.append(genFeature.getCapName());
											stringBuffer.append(TEXT_1035);
											stringBuffer.append(genFeature.getUncapName());
											stringBuffer.append(TEXT_1036);
										}
									}
									if (genFeature.isReferenceType()) {
										stringBuffer.append(TEXT_1037);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_1038);
										if (!genModel.isVirtualDelegation()) {
											if (genClass.isESetFlag(genFeature)) {
												stringBuffer.append(TEXT_1039);
												stringBuffer.append(genClass.getESetFlagsField(genFeature));
												stringBuffer.append(TEXT_1040);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1041);
											} else {
												stringBuffer.append(TEXT_1042);
												stringBuffer.append(genFeature.getUncapName());
												stringBuffer.append(TEXT_1043);
											}
										}
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_1044);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
											stringBuffer.append(TEXT_1045);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.common.notify.Notification"));
											stringBuffer.append(TEXT_1046);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_1047);
											if (genModel.isVirtualDelegation()) {
												stringBuffer.append(TEXT_1048);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_1049);
											} else {
												stringBuffer.append(TEXT_1050);
												stringBuffer.append(genFeature.getCapName());
											}
											stringBuffer.append(TEXT_1051);
											if (genModel.isVirtualDelegation()) {
												stringBuffer.append(TEXT_1052);
											} else {
												stringBuffer.append(TEXT_1053);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_1054);
											}
											stringBuffer.append(TEXT_1055);
										}
									} else {
										if (genClass.isFlag(genFeature)) {
											if (genFeature.isBooleanType()) {
												stringBuffer.append(TEXT_1056);
												stringBuffer.append(genFeature.getEDefault());
												stringBuffer.append(TEXT_1057);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_1058);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1059);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_1060);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1061);
											} else {
												stringBuffer.append(TEXT_1062);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_1063);
												stringBuffer.append(genClass.getFlagsField(genFeature));
												stringBuffer.append(TEXT_1064);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1065);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1066);
											}
										} else if (!genModel.isVirtualDelegation() || genFeature.isPrimitiveType()) {
											stringBuffer.append(TEXT_1067);
											stringBuffer.append(genFeature.getSafeName());
											stringBuffer.append(TEXT_1068);
											stringBuffer.append(genFeature.getEDefault());
											stringBuffer.append(TEXT_1069);
										}
										if (!genModel.isVirtualDelegation() || genFeature.isPrimitiveType()) {
											if (genClass.isESetFlag(genFeature)) {
												stringBuffer.append(TEXT_1070);
												stringBuffer.append(genClass.getESetFlagsField(genFeature));
												stringBuffer.append(TEXT_1071);
												stringBuffer.append(genFeature.getUpperName());
												stringBuffer.append(TEXT_1072);
											} else {
												stringBuffer.append(TEXT_1073);
												stringBuffer.append(genFeature.getUncapName());
												stringBuffer.append(TEXT_1074);
											}
										}
										if (!genModel.isSuppressNotification()) {
											stringBuffer.append(TEXT_1075);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.ecore.impl.ENotificationImpl"));
											stringBuffer.append(TEXT_1076);
											stringBuffer.append(genModel
													.getImportedName("org.eclipse.emf.common.notify.Notification"));
											stringBuffer.append(TEXT_1077);
											stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
											stringBuffer.append(positiveOffsetCorrection);
											stringBuffer.append(TEXT_1078);
											if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_1079);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_1080);
												stringBuffer.append(genFeature.getEDefault());
											} else {
												stringBuffer.append(TEXT_1081);
												stringBuffer.append(genFeature.getCapName());
											}
											stringBuffer.append(TEXT_1082);
											stringBuffer.append(genFeature.getEDefault());
											stringBuffer.append(TEXT_1083);
											if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
												stringBuffer.append(TEXT_1084);
											} else {
												stringBuffer.append(TEXT_1085);
												stringBuffer.append(genFeature.getCapName());
												stringBuffer.append(TEXT_1086);
											}
											stringBuffer.append(TEXT_1087);
										}
									}
								}
							} else if (genFeature.hasDelegateFeature()) {
								GenFeature delegateFeature = genFeature.getDelegateFeature();
								if (delegateFeature.isWrappedFeatureMapType()) {
									stringBuffer.append(TEXT_1088);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_1089);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_1090);
									stringBuffer.append(delegateFeature.getAccessorName());
									stringBuffer.append(TEXT_1091);
									stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
									stringBuffer.append(TEXT_1092);
								} else {
									stringBuffer.append(TEXT_1093);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_1094);
									stringBuffer.append(delegateFeature.getAccessorName());
									stringBuffer.append(TEXT_1095);
									stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
									stringBuffer.append(TEXT_1096);
								}
							} else if (genClass.getUnsetAccessorOperation(genFeature) != null) {
								stringBuffer.append(TEXT_1097);
								stringBuffer.append(genClass.getUnsetAccessorOperation(genFeature)
										.getBody(genModel.getIndentation(stringBuffer)));
							} else {
								stringBuffer.append(TEXT_1098);
								stringBuffer.append(genFeature.getFormattedName());
								stringBuffer.append(TEXT_1099);
								stringBuffer.append(genFeature.getFeatureKind());
								stringBuffer.append(TEXT_1100);
								// Class/unsetGenFeature.todo.override.javajetinc
							}
							stringBuffer.append(TEXT_1101);
						}
						// Class/unsetGenFeature.override.javajetinc
					}
					if (genFeature.isIsSet() && (isImplementation || !genFeature.isSuppressedIsSetVisibility())) {
						if (isInterface) {
							stringBuffer.append(TEXT_1102);
							stringBuffer.append(genClass.getQualifiedInterfaceName());
							stringBuffer.append(TEXT_1103);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1104);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_1105);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_1106);
							stringBuffer.append(TEXT_1107);
							stringBuffer.append(genFeature.getFormattedName());
							stringBuffer.append(TEXT_1108);
							stringBuffer.append(genFeature.getFeatureKind());
							stringBuffer.append(TEXT_1109);
							if (genFeature.isChangeable() && !genFeature.isSuppressedUnsetVisibility()) {
								stringBuffer.append(TEXT_1110);
								stringBuffer.append(genFeature.getAccessorName());
								stringBuffer.append(TEXT_1111);
							}
							stringBuffer.append(TEXT_1112);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1113);
							if (!genFeature.isListType() && genFeature.isChangeable()
									&& !genFeature.isSuppressedSetVisibility()) {
								stringBuffer.append(TEXT_1114);
								stringBuffer.append(genFeature.getAccessorName());
								stringBuffer.append(TEXT_1115);
								stringBuffer.append(genFeature.getRawImportedBoundType());
								stringBuffer.append(TEXT_1116);
							}
							stringBuffer.append(TEXT_1117);
							// Class/isSetGenFeature.javadoc.override.javajetinc
						} else {
							stringBuffer.append(TEXT_1118);
							if (isJDK50) { // Class/isSetGenFeature.annotations.insert.javajetinc
							}
						}
						if (!isImplementation) {
							stringBuffer.append(TEXT_1119);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_1120);
						} else {
							stringBuffer.append(TEXT_1121);
							stringBuffer.append(genFeature.getAccessorName());
							if (genClass.hasCollidingIsSetAccessorOperation(genFeature)) {
								stringBuffer.append(TEXT_1122);
							}
							stringBuffer.append(TEXT_1123);
							if (genModel.isDynamicDelegation()) {
								stringBuffer.append(TEXT_1124);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(TEXT_1125);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_1126);
							} else if (genModel.isReflectiveDelegation()) {
								stringBuffer.append(TEXT_1127);
								stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
								stringBuffer.append(TEXT_1128);
							} else if (genFeature.hasSettingDelegate()) {
								stringBuffer.append(TEXT_1129);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_1130);
							} else if (!genFeature.isVolatile()) {
								if (genFeature.isListType()) {
									if (genModel.isVirtualDelegation()) {
										stringBuffer.append(TEXT_1131);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_1132);
										stringBuffer.append(genFeature.getSafeName());
										stringBuffer.append(TEXT_1133);
										stringBuffer.append(genFeature.getImportedType(genClass));
										stringBuffer.append(TEXT_1134);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_1135);
									}
									stringBuffer.append(TEXT_1136);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_1137);
									stringBuffer.append(
											genModel.getImportedName("org.eclipse.emf.ecore.util.InternalEList"));
									stringBuffer.append(TEXT_1138);
									stringBuffer.append(singleWildcard);
									stringBuffer.append(TEXT_1139);
									stringBuffer.append(genFeature.getSafeName());
									stringBuffer.append(TEXT_1140);
								} else {
									if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
										stringBuffer.append(TEXT_1141);
										stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
										stringBuffer.append(positiveOffsetCorrection);
										stringBuffer.append(TEXT_1142);
									} else if (genClass.isESetFlag(genFeature)) {
										stringBuffer.append(TEXT_1143);
										stringBuffer.append(genClass.getESetFlagsField(genFeature));
										stringBuffer.append(TEXT_1144);
										stringBuffer.append(genFeature.getUpperName());
										stringBuffer.append(TEXT_1145);
									} else {
										stringBuffer.append(TEXT_1146);
										stringBuffer.append(genFeature.getUncapName());
										stringBuffer.append(TEXT_1147);
									}
								}
							} else if (genFeature.hasDelegateFeature()) {
								GenFeature delegateFeature = genFeature.getDelegateFeature();
								if (delegateFeature.isWrappedFeatureMapType()) {
									stringBuffer.append(TEXT_1148);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_1149);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_1150);
									stringBuffer.append(delegateFeature.getAccessorName());
									stringBuffer.append(TEXT_1151);
									stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
									stringBuffer.append(TEXT_1152);
								} else {
									stringBuffer.append(TEXT_1153);
									stringBuffer
											.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
									stringBuffer.append(TEXT_1154);
									stringBuffer.append(delegateFeature.getAccessorName());
									stringBuffer.append(TEXT_1155);
									stringBuffer.append(genFeature.getQualifiedFeatureAccessor());
									stringBuffer.append(TEXT_1156);
								}
							} else if (genClass.getIsSetAccessorOperation(genFeature) != null) {
								stringBuffer.append(TEXT_1157);
								stringBuffer.append(genClass.getIsSetAccessorOperation(genFeature)
										.getBody(genModel.getIndentation(stringBuffer)));
							} else {
								stringBuffer.append(TEXT_1158);
								stringBuffer.append(genFeature.getFormattedName());
								stringBuffer.append(TEXT_1159);
								stringBuffer.append(genFeature.getFeatureKind());
								stringBuffer.append(TEXT_1160);
								// Class/isSetGenFeature.todo.override.javajetinc
							}
							stringBuffer.append(TEXT_1161);
						}
						// Class/isSetGenFeature.override.javajetinc
					}
					// Class/genFeature.override.javajetinc
				} // for
			}
		}.run();
		for (GenOperation genOperation : (isImplementation ? genClass.getImplementedGenOperations()
				: genClass.getDeclaredGenOperations())) {
			if (isImplementation) {
				if (genOperation.isInvariant() && genOperation.hasInvariantExpression()) {
					stringBuffer.append(TEXT_1162);
					stringBuffer.append(genOperation.getName());
					stringBuffer.append(TEXT_1163);
					stringBuffer.append(genOperation.getParameterTypes(", "));
					stringBuffer.append(TEXT_1164);
					stringBuffer.append(genOperation.getFormattedName());
					stringBuffer.append(TEXT_1165);
					stringBuffer.append(genOperation.getName());
					stringBuffer.append(TEXT_1166);
					stringBuffer.append(genOperation.getParameterTypes(", "));
					stringBuffer.append(TEXT_1167);
					stringBuffer.append(genModel.getImportedName("java.lang.String"));
					stringBuffer.append(TEXT_1168);
					stringBuffer
							.append(CodeGenUtil.upperName(genClass.getUniqueName(genOperation), genModel.getLocale()));
					stringBuffer.append(TEXT_1169);
					stringBuffer.append(genOperation.getInvariantExpression("\t\t"));
					stringBuffer.append(TEXT_1170);
					stringBuffer.append(genModel.getNonNLS());
					stringBuffer.append(TEXT_1171);
				} else if (genOperation.hasInvocationDelegate()) {
					stringBuffer.append(TEXT_1172);
					stringBuffer.append(genOperation.getName());
					stringBuffer.append(TEXT_1173);
					stringBuffer.append(genOperation.getParameterTypes(", "));
					stringBuffer.append(TEXT_1174);
					stringBuffer.append(genOperation.getFormattedName());
					stringBuffer.append(TEXT_1175);
					stringBuffer.append(genOperation.getName());
					stringBuffer.append(TEXT_1176);
					stringBuffer.append(genOperation.getParameterTypes(", "));
					stringBuffer.append(TEXT_1177);
					stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EOperation"));
					stringBuffer.append(TEXT_1178);
					stringBuffer
							.append(CodeGenUtil.upperName(genClass.getUniqueName(genOperation), genModel.getLocale()));
					stringBuffer.append(TEXT_1179);
					stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EOperation"));
					stringBuffer.append(TEXT_1180);
					stringBuffer.append(genOperation.getQualifiedOperationAccessor());
					stringBuffer.append(TEXT_1181);
				}
			}
			if (isInterface) {
				stringBuffer.append(TEXT_1182);
				stringBuffer.append(TEXT_1183);
				if (genOperation.hasDocumentation() || genOperation.hasParameterDocumentation()) {
					stringBuffer.append(TEXT_1184);
					if (genOperation.hasDocumentation()) {
						stringBuffer.append(TEXT_1185);
						stringBuffer.append(genOperation.getDocumentation(genModel.getIndentation(stringBuffer)));
					}
					for (GenParameter genParameter : genOperation.getGenParameters()) {
						if (genParameter.hasDocumentation()) {
							String documentation = genParameter.getDocumentation("");
							if (documentation.contains("\n") || documentation.contains("\r")) {
								stringBuffer.append(TEXT_1186);
								stringBuffer.append(genParameter.getName());
								stringBuffer.append(TEXT_1187);
								stringBuffer
										.append(genParameter.getDocumentation(genModel.getIndentation(stringBuffer)));
							} else {
								stringBuffer.append(TEXT_1188);
								stringBuffer.append(genParameter.getName());
								stringBuffer.append(TEXT_1189);
								stringBuffer
										.append(genParameter.getDocumentation(genModel.getIndentation(stringBuffer)));
							}
						}
					}
					stringBuffer.append(TEXT_1190);
				}
				if (!genModel.isSuppressEMFModelTags()) {
					boolean first = true;
					for (StringTokenizer stringTokenizer = new StringTokenizer(genOperation.getModelInfo(),
							"\n\r"); stringTokenizer.hasMoreTokens();) {
						String modelInfo = stringTokenizer.nextToken();
						if (first) {
							first = false;
							stringBuffer.append(TEXT_1191);
							stringBuffer.append(modelInfo);
						} else {
							stringBuffer.append(TEXT_1192);
							stringBuffer.append(modelInfo);
						}
					}
					if (first) {
						stringBuffer.append(TEXT_1193);
					}
				}
				stringBuffer.append(TEXT_1194);
				// Class/genOperation.javadoc.override.javajetinc
			} else {
				stringBuffer.append(TEXT_1195);
				if (isJDK50) { // Class/genOperation.annotations.insert.javajetinc
				}
			}
			if (!isImplementation) {
				stringBuffer.append(TEXT_1196);
				stringBuffer.append(genOperation.getTypeParameters(genClass));
				stringBuffer.append(genOperation.getImportedType(genClass));
				stringBuffer.append(TEXT_1197);
				stringBuffer.append(genOperation.getName());
				stringBuffer.append(TEXT_1198);
				stringBuffer.append(genOperation.getParameters(genClass));
				stringBuffer.append(TEXT_1199);
				stringBuffer.append(genOperation.getThrows(genClass));
				stringBuffer.append(TEXT_1200);
			} else {
				if (genModel.useGenerics() && !genOperation.hasBody() && !genOperation.isInvariant()
						&& genOperation.hasInvocationDelegate() && genOperation.isUncheckedCast(genClass)) {
					stringBuffer.append(TEXT_1201);
				}
				stringBuffer.append(TEXT_1202);
				stringBuffer.append(genOperation.getTypeParameters(genClass));
				stringBuffer.append(genOperation.getImportedType(genClass));
				stringBuffer.append(TEXT_1203);
				stringBuffer.append(genOperation.getName());
				stringBuffer.append(TEXT_1204);
				stringBuffer.append(genOperation.getParameters(genClass));
				stringBuffer.append(TEXT_1205);
				stringBuffer.append(genOperation.getThrows(genClass));
				stringBuffer.append(TEXT_1206);
				if (genOperation.hasBody()) {
					stringBuffer.append(TEXT_1207);
					stringBuffer.append(genOperation.getBody(genModel.getIndentation(stringBuffer)));
				} else if (genOperation.isInvariant()) {
					GenClass opClass = genOperation.getGenClass();
					String diagnostics = genOperation.getGenParameters().get(0).getName();
					String context = genOperation.getGenParameters().get(1).getName();
					if (genOperation.hasInvariantExpression()) {
						stringBuffer.append(TEXT_1208);
						stringBuffer.append(opClass.getGenPackage().getImportedValidatorClassName());
						stringBuffer.append(TEXT_1209);
						stringBuffer.append(genClass.getQualifiedClassifierAccessor());
						stringBuffer.append(TEXT_1210);
						stringBuffer.append(diagnostics);
						stringBuffer.append(TEXT_1211);
						stringBuffer.append(context);
						stringBuffer.append(TEXT_1212);
						stringBuffer.append(genOperation.getValidationDelegate());
						stringBuffer.append(TEXT_1213);
						stringBuffer.append(genModel.getNonNLS());
						stringBuffer.append(TEXT_1214);
						stringBuffer.append(genOperation.getQualifiedOperationAccessor());
						stringBuffer.append(TEXT_1215);
						stringBuffer.append(
								CodeGenUtil.upperName(genClass.getUniqueName(genOperation), genModel.getLocale()));
						stringBuffer.append(TEXT_1216);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.Diagnostic"));
						stringBuffer.append(TEXT_1217);
						stringBuffer.append(opClass.getGenPackage().getImportedValidatorClassName());
						stringBuffer.append(TEXT_1218);
						stringBuffer.append(opClass.getGenPackage().getImportedValidatorClassName());
						stringBuffer.append(TEXT_1219);
						stringBuffer.append(opClass.getOperationID(genOperation));
						stringBuffer.append(TEXT_1220);
					} else {
						stringBuffer.append(TEXT_1221);
						stringBuffer.append(diagnostics);
						stringBuffer.append(TEXT_1222);
						stringBuffer.append(diagnostics);
						stringBuffer.append(TEXT_1223);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicDiagnostic"));
						stringBuffer.append(TEXT_1224);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.Diagnostic"));
						stringBuffer.append(TEXT_1225);
						stringBuffer.append(opClass.getGenPackage().getImportedValidatorClassName());
						stringBuffer.append(TEXT_1226);
						stringBuffer.append(opClass.getGenPackage().getImportedValidatorClassName());
						stringBuffer.append(TEXT_1227);
						stringBuffer.append(opClass.getOperationID(genOperation));
						stringBuffer.append(TEXT_1228);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.plugin.EcorePlugin"));
						stringBuffer.append(TEXT_1229);
						stringBuffer.append(genOperation.getName());
						stringBuffer.append(TEXT_1230);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.EObjectValidator"));
						stringBuffer.append(TEXT_1231);
						stringBuffer.append(context);
						stringBuffer.append(TEXT_1232);
						stringBuffer.append(genModel.getNonNLS());
						stringBuffer.append(genModel.getNonNLS(2));
						stringBuffer.append(TEXT_1233);
					}
				} else if (genOperation.hasInvocationDelegate()) {
					int size = genOperation.getGenParameters().size();
					stringBuffer.append(TEXT_1234);
					if (genOperation.isVoid()) {
						stringBuffer.append(TEXT_1235);
						stringBuffer.append(
								CodeGenUtil.upperName(genClass.getUniqueName(genOperation), genModel.getLocale()));
						stringBuffer.append(TEXT_1236);
						if (size > 0) {
							stringBuffer.append(TEXT_1237);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicEList"));
							stringBuffer.append(TEXT_1238);
							stringBuffer.append(size);
							stringBuffer.append(TEXT_1239);
							stringBuffer.append(genOperation.getParametersArray(genClass));
							stringBuffer.append(TEXT_1240);
						} else {
							stringBuffer.append(TEXT_1241);
						}
						stringBuffer.append(TEXT_1242);
					} else {
						stringBuffer.append(TEXT_1243);
						if (!isJDK50 && genOperation.isPrimitiveType()) {
							stringBuffer.append(TEXT_1244);
						}
						stringBuffer.append(TEXT_1245);
						stringBuffer.append(genOperation.getObjectType(genClass));
						stringBuffer.append(TEXT_1246);
						stringBuffer.append(
								CodeGenUtil.upperName(genClass.getUniqueName(genOperation), genModel.getLocale()));
						stringBuffer.append(TEXT_1247);
						if (size > 0) {
							stringBuffer.append(TEXT_1248);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.BasicEList"));
							stringBuffer.append(TEXT_1249);
							stringBuffer.append(size);
							stringBuffer.append(TEXT_1250);
							stringBuffer.append(genOperation.getParametersArray(genClass));
							stringBuffer.append(TEXT_1251);
						} else {
							stringBuffer.append(TEXT_1252);
						}
						stringBuffer.append(TEXT_1253);
						if (!isJDK50 && genOperation.isPrimitiveType()) {
							stringBuffer.append(TEXT_1254);
							stringBuffer.append(genOperation.getPrimitiveValueFunction());
							stringBuffer.append(TEXT_1255);
						}
						stringBuffer.append(TEXT_1256);
					}
					stringBuffer.append(TEXT_1257);
					stringBuffer.append(
							genModel.getImportedName(isGWT ? "org.eclipse.emf.common.util.InvocationTargetException"
									: "java.lang.reflect.InvocationTargetException"));
					stringBuffer.append(TEXT_1258);
					stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.WrappedException"));
					stringBuffer.append(TEXT_1259);
				} else {
					// SDM based method implementation
					stringBuffer.append(TEXT_1260);
					// Class/implementedGenOperation.todo.override.javajetinc
				}
				stringBuffer.append(TEXT_1261);
			}
			// Class/implementedGenOperation.override.javajetinc
		} // for
		if (isImplementation && !genModel.isReflectiveDelegation()
				&& genClass.implementsAny(genClass.getEInverseAddGenFeatures())) {
			stringBuffer.append(TEXT_1262);
			if (genModel.useGenerics()) {
				for (GenFeature genFeature : genClass.getEInverseAddGenFeatures()) {
					if (genFeature.isUncheckedCast(genClass)) {
						stringBuffer.append(TEXT_1263);
						break;
					}
				}
			}
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1264);
			}
			stringBuffer.append(TEXT_1265);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
			stringBuffer.append(TEXT_1266);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
			stringBuffer.append(TEXT_1267);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
			stringBuffer.append(TEXT_1268);
			stringBuffer.append(negativeOffsetCorrection);
			stringBuffer.append(TEXT_1269);
			for (GenFeature genFeature : genClass.getEInverseAddGenFeatures()) {
				stringBuffer.append(TEXT_1270);
				stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
				stringBuffer.append(TEXT_1271);
				if (genFeature.isListType()) {
					String cast = "(" + genModel.getImportedName("org.eclipse.emf.ecore.util.InternalEList")
							+ (!genModel.useGenerics() ? ")"
									: "<" + genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject") + ">)("
											+ genModel.getImportedName("org.eclipse.emf.ecore.util.InternalEList")
											+ "<?>)");
					if (genFeature.isMapType() && genFeature.isEffectiveSuppressEMFTypes()) {
						stringBuffer.append(TEXT_1272);
						stringBuffer.append(cast);
						stringBuffer.append(TEXT_1273);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.EMap"));
						stringBuffer.append(TEXT_1274);
						stringBuffer.append(genFeature.getImportedMapTemplateArguments(genClass));
						stringBuffer.append(TEXT_1275);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1276);
					} else {
						stringBuffer.append(TEXT_1277);
						stringBuffer.append(cast);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1278);
					}
				} else if (genFeature.isContainer()) {
					stringBuffer.append(TEXT_1279);
					if (genFeature.isBasicSet()) {
						stringBuffer.append(TEXT_1280);
						stringBuffer.append(genFeature.getAccessorName());
						stringBuffer.append(TEXT_1281);
						stringBuffer.append(genFeature.getImportedType(genClass));
						stringBuffer.append(TEXT_1282);
					} else {
						stringBuffer.append(TEXT_1283);
						stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
						stringBuffer.append(positiveOffsetCorrection);
						stringBuffer.append(TEXT_1284);
					}
				} else {
					if (genClass.getImplementingGenModel(genFeature).isVirtualDelegation()) {
						stringBuffer.append(TEXT_1285);
						stringBuffer.append(genFeature.getImportedType(genClass));
						stringBuffer.append(TEXT_1286);
						stringBuffer.append(genFeature.getSafeName());
						stringBuffer.append(TEXT_1287);
						stringBuffer.append(genFeature.getImportedType(genClass));
						stringBuffer.append(TEXT_1288);
						stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
						stringBuffer.append(positiveOffsetCorrection);
						stringBuffer.append(TEXT_1289);
					} else if (genFeature.isVolatile()
							|| genClass.getImplementingGenModel(genFeature).isDynamicDelegation()) {
						stringBuffer.append(TEXT_1290);
						stringBuffer.append(genFeature.getImportedType(genClass));
						stringBuffer.append(TEXT_1291);
						stringBuffer.append(genFeature.getSafeName());
						stringBuffer.append(TEXT_1292);
						if (genFeature.isResolveProxies()) {
							stringBuffer.append(TEXT_1293);
							stringBuffer.append(genFeature.getAccessorName());
						} else {
							stringBuffer.append(genFeature.getGetAccessor());
						}
						stringBuffer.append(TEXT_1294);
					}
					stringBuffer.append(TEXT_1295);
					stringBuffer.append(genFeature.getSafeName());
					stringBuffer.append(TEXT_1296);
					if (genFeature.isEffectiveContains()) {
						stringBuffer.append(TEXT_1297);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
						stringBuffer.append(TEXT_1298);
						stringBuffer.append(genFeature.getSafeName());
						stringBuffer.append(TEXT_1299);
						stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
						stringBuffer.append(negativeOffsetCorrection);
						stringBuffer.append(TEXT_1300);
					} else {
						GenFeature reverseFeature = genFeature.getReverse();
						GenClass targetClass = reverseFeature.getGenClass();
						String reverseOffsetCorrection = targetClass.hasOffsetCorrection()
								? " + " + genClass.getOffsetCorrectionField(genFeature)
								: "";
						stringBuffer.append(TEXT_1301);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
						stringBuffer.append(TEXT_1302);
						stringBuffer.append(genFeature.getSafeName());
						stringBuffer.append(TEXT_1303);
						stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
						stringBuffer.append(reverseOffsetCorrection);
						stringBuffer.append(TEXT_1304);
						stringBuffer.append(targetClass.getRawImportedInterfaceName());
						stringBuffer.append(TEXT_1305);
					}
					stringBuffer.append(TEXT_1306);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1307);
					stringBuffer.append(genFeature.getImportedType(genClass));
					stringBuffer.append(TEXT_1308);
				}
			}
			stringBuffer.append(TEXT_1309);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1310);
			} else {
				stringBuffer.append(TEXT_1311);
			}
			stringBuffer.append(TEXT_1312);
		}
		if (isImplementation && !genModel.isReflectiveDelegation()
				&& genClass.implementsAny(genClass.getEInverseRemoveGenFeatures())) {
			stringBuffer.append(TEXT_1313);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1314);
			}
			stringBuffer.append(TEXT_1315);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
			stringBuffer.append(TEXT_1316);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.InternalEObject"));
			stringBuffer.append(TEXT_1317);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
			stringBuffer.append(TEXT_1318);
			stringBuffer.append(negativeOffsetCorrection);
			stringBuffer.append(TEXT_1319);
			for (GenFeature genFeature : genClass.getEInverseRemoveGenFeatures()) {
				stringBuffer.append(TEXT_1320);
				stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
				stringBuffer.append(TEXT_1321);
				if (genFeature.isListType()) {
					if (genFeature.isMapType() && genFeature.isEffectiveSuppressEMFTypes()) {
						stringBuffer.append(TEXT_1322);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.InternalEList"));
						stringBuffer.append(singleWildcard);
						stringBuffer.append(TEXT_1323);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.EMap"));
						stringBuffer.append(TEXT_1324);
						stringBuffer.append(genFeature.getImportedMapTemplateArguments(genClass));
						stringBuffer.append(TEXT_1325);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1326);
					} else if (genFeature.isWrappedFeatureMapType()) {
						stringBuffer.append(TEXT_1327);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.InternalEList"));
						stringBuffer.append(singleWildcard);
						stringBuffer.append(TEXT_1328);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
						stringBuffer.append(TEXT_1329);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1330);
					} else {
						stringBuffer.append(TEXT_1331);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.InternalEList"));
						stringBuffer.append(singleWildcard);
						stringBuffer.append(TEXT_1332);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1333);
					}
				} else if (genFeature.isContainer() && !genFeature.isBasicSet()) {
					stringBuffer.append(TEXT_1334);
					stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
					stringBuffer.append(positiveOffsetCorrection);
					stringBuffer.append(TEXT_1335);
				} else if (genFeature.isUnsettable()) {
					stringBuffer.append(TEXT_1336);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1337);
				} else {
					stringBuffer.append(TEXT_1338);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1339);
				}
			}
			stringBuffer.append(TEXT_1340);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1341);
			} else {
				stringBuffer.append(TEXT_1342);
			}
			stringBuffer.append(TEXT_1343);
		}
		if (isImplementation && !genModel.isReflectiveDelegation()
				&& genClass.implementsAny(genClass.getEBasicRemoveFromContainerGenFeatures())) {
			stringBuffer.append(TEXT_1344);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1345);
			}
			stringBuffer.append(TEXT_1346);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
			stringBuffer.append(TEXT_1347);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.notify.NotificationChain"));
			stringBuffer.append(TEXT_1348);
			stringBuffer.append(negativeOffsetCorrection);
			stringBuffer.append(TEXT_1349);
			for (GenFeature genFeature : genClass.getEBasicRemoveFromContainerGenFeatures()) {
				GenFeature reverseFeature = genFeature.getReverse();
				GenClass targetClass = reverseFeature.getGenClass();
				String reverseOffsetCorrection = targetClass.hasOffsetCorrection()
						? " + " + genClass.getOffsetCorrectionField(genFeature)
						: "";
				stringBuffer.append(TEXT_1350);
				stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
				stringBuffer.append(TEXT_1351);
				stringBuffer.append(targetClass.getQualifiedFeatureID(reverseFeature));
				stringBuffer.append(reverseOffsetCorrection);
				stringBuffer.append(TEXT_1352);
				stringBuffer.append(targetClass.getRawImportedInterfaceName());
				stringBuffer.append(TEXT_1353);
			}
			stringBuffer.append(TEXT_1354);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1355);
			} else {
				stringBuffer.append(TEXT_1356);
			}
			stringBuffer.append(TEXT_1357);
		}
		if (isImplementation && !genModel.isReflectiveDelegation()
				&& genClass.implementsAny(genClass.getEGetGenFeatures())) {
			stringBuffer.append(TEXT_1358);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1359);
			}
			stringBuffer.append(TEXT_1360);
			stringBuffer.append(negativeOffsetCorrection);
			stringBuffer.append(TEXT_1361);
			for (GenFeature genFeature : genClass.getEGetGenFeatures()) {
				stringBuffer.append(TEXT_1362);
				stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
				stringBuffer.append(TEXT_1363);
				if (genFeature.isPrimitiveType()) {
					if (isJDK50) {
						stringBuffer.append(TEXT_1364);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1365);
					} else if (genFeature.isBooleanType()) {
						stringBuffer.append(TEXT_1366);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1367);
					} else {
						stringBuffer.append(TEXT_1368);
						stringBuffer.append(genFeature.getObjectType(genClass));
						stringBuffer.append(TEXT_1369);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1370);
					}
				} else if (genFeature.isResolveProxies() && !genFeature.isListType()) {
					stringBuffer.append(TEXT_1371);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_1372);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1373);
				} else if (genFeature.isMapType()) {
					if (genFeature.isEffectiveSuppressEMFTypes()) {
						stringBuffer.append(TEXT_1374);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.EMap"));
						stringBuffer.append(TEXT_1375);
						stringBuffer.append(genFeature.getImportedMapTemplateArguments(genClass));
						stringBuffer.append(TEXT_1376);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1377);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1378);
					} else {
						stringBuffer.append(TEXT_1379);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1380);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1381);
					}
				} else if (genFeature.isWrappedFeatureMapType()) {
					stringBuffer.append(TEXT_1382);
					stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
					stringBuffer.append(TEXT_1383);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_1384);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_1385);
				} else if (genFeature.isFeatureMapType()) {
					stringBuffer.append(TEXT_1386);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_1387);
					stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
					stringBuffer.append(TEXT_1388);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_1389);
				} else {
					stringBuffer.append(TEXT_1390);
					stringBuffer.append(genFeature.getGetAccessor());
					stringBuffer.append(TEXT_1391);
				}
			}
			stringBuffer.append(TEXT_1392);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1393);
			} else {
				stringBuffer.append(TEXT_1394);
			}
			stringBuffer.append(TEXT_1395);
		}
		if (isImplementation && !genModel.isReflectiveDelegation()
				&& genClass.implementsAny(genClass.getESetGenFeatures())) {
			stringBuffer.append(TEXT_1396);
			if (genModel.useGenerics()) {
				for (GenFeature genFeature : genClass.getESetGenFeatures()) {
					if (genFeature.isUncheckedCast(genClass) && !genFeature.isFeatureMapType()
							&& !genFeature.isMapType()) {
						stringBuffer.append(TEXT_1397);
						break;
					}
				}
			}
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1398);
			}
			stringBuffer.append(TEXT_1399);
			stringBuffer.append(negativeOffsetCorrection);
			stringBuffer.append(TEXT_1400);
			for (GenFeature genFeature : genClass.getESetGenFeatures()) {
				stringBuffer.append(TEXT_1401);
				stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
				stringBuffer.append(TEXT_1402);
				if (genFeature.isListType()) {
					if (genFeature.isWrappedFeatureMapType()) {
						stringBuffer.append(TEXT_1403);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
						stringBuffer.append(TEXT_1404);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
						stringBuffer.append(TEXT_1405);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1406);
					} else if (genFeature.isFeatureMapType()) {
						stringBuffer.append(TEXT_1407);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
						stringBuffer.append(TEXT_1408);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1409);
					} else if (genFeature.isMapType()) {
						if (genFeature.isEffectiveSuppressEMFTypes()) {
							stringBuffer.append(TEXT_1410);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EStructuralFeature"));
							stringBuffer.append(TEXT_1411);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.EMap"));
							stringBuffer.append(TEXT_1412);
							stringBuffer.append(genFeature.getImportedMapTemplateArguments(genClass));
							stringBuffer.append(TEXT_1413);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1414);
						} else {
							stringBuffer.append(TEXT_1415);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EStructuralFeature"));
							stringBuffer.append(TEXT_1416);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1417);
						}
					} else {
						stringBuffer.append(TEXT_1418);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1419);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1420);
						stringBuffer.append(genModel.getImportedName("java.util.Collection"));
						if (isJDK50) {
							stringBuffer.append(TEXT_1421);
							stringBuffer.append(genFeature.getListItemType(genClass));
							stringBuffer.append(TEXT_1422);
						}
						stringBuffer.append(TEXT_1423);
					}
				} else if (!isJDK50 && genFeature.isPrimitiveType()) {
					stringBuffer.append(TEXT_1424);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1425);
					stringBuffer.append(genFeature.getObjectType(genClass));
					stringBuffer.append(TEXT_1426);
					stringBuffer.append(genFeature.getPrimitiveValueFunction());
					stringBuffer.append(TEXT_1427);
				} else {
					stringBuffer.append(TEXT_1428);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1429);
					if (genFeature.getTypeGenDataType() == null || !genFeature.getTypeGenDataType().isObjectType()
							|| !genFeature.getRawType().equals(genFeature.getType(genClass))) {
						stringBuffer.append(TEXT_1430);
						stringBuffer.append(genFeature.getObjectType(genClass));
						stringBuffer.append(TEXT_1431);
					}
					stringBuffer.append(TEXT_1432);
				}
				stringBuffer.append(TEXT_1433);
			}
			stringBuffer.append(TEXT_1434);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1435);
			} else {
				stringBuffer.append(TEXT_1436);
			}
			stringBuffer.append(TEXT_1437);
		}
		if (isImplementation && !genModel.isReflectiveDelegation()
				&& genClass.implementsAny(genClass.getEUnsetGenFeatures())) {
			stringBuffer.append(TEXT_1438);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1439);
			}
			stringBuffer.append(TEXT_1440);
			stringBuffer.append(negativeOffsetCorrection);
			stringBuffer.append(TEXT_1441);
			for (GenFeature genFeature : genClass.getEUnsetGenFeatures()) {
				stringBuffer.append(TEXT_1442);
				stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
				stringBuffer.append(TEXT_1443);
				if (genFeature.isListType() && !genFeature.isUnsettable()) {
					if (genFeature.isWrappedFeatureMapType()) {
						stringBuffer.append(TEXT_1444);
						stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
						stringBuffer.append(TEXT_1445);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1446);
					} else {
						stringBuffer.append(TEXT_1447);
						stringBuffer.append(genFeature.getGetAccessor());
						stringBuffer.append(TEXT_1448);
					}
				} else if (genFeature.isUnsettable()) {
					stringBuffer.append(TEXT_1449);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1450);
				} else if (!genFeature.hasEDefault()) {
					stringBuffer.append(TEXT_1451);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1452);
					stringBuffer.append(genFeature.getImportedType(genClass));
					stringBuffer.append(TEXT_1453);
				} else {
					stringBuffer.append(TEXT_1454);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1455);
					stringBuffer.append(genFeature.getEDefault());
					stringBuffer.append(TEXT_1456);
				}
				stringBuffer.append(TEXT_1457);
			}
			stringBuffer.append(TEXT_1458);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1459);
			} else {
				stringBuffer.append(TEXT_1460);
			}
			stringBuffer.append(TEXT_1461);
			// Class/eUnset.override.javajetinc
		}
		if (isImplementation && !genModel.isReflectiveDelegation()
				&& genClass.implementsAny(genClass.getEIsSetGenFeatures())) {
			stringBuffer.append(TEXT_1462);
			if (genModel.useGenerics()) {
				for (GenFeature genFeature : genClass.getEIsSetGenFeatures()) {
					if (genFeature.isListType() && !genFeature.isUnsettable() && !genFeature.isWrappedFeatureMapType()
							&& !genClass.isField(genFeature) && genFeature.isField()
							&& genClass.getImplementingGenModel(genFeature).isVirtualDelegation()) {
						stringBuffer.append(TEXT_1463);
						break;
					}
				}
			}
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1464);
			}
			stringBuffer.append(TEXT_1465);
			stringBuffer.append(negativeOffsetCorrection);
			stringBuffer.append(TEXT_1466);
			for (GenFeature genFeature : genClass.getEIsSetGenFeatures()) {
				String safeNameAccessor = genFeature.getSafeName();
				if ("featureID".equals(safeNameAccessor)) {
					safeNameAccessor = "this." + safeNameAccessor;
				}
				stringBuffer.append(TEXT_1467);
				stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
				stringBuffer.append(TEXT_1468);
				if (genFeature.hasSettingDelegate()) {
					if (genFeature.isUnsettable()) {
						stringBuffer.append(TEXT_1469);
						stringBuffer.append(genFeature.getAccessorName());
						stringBuffer.append(TEXT_1470);
					} else {
						stringBuffer.append(TEXT_1471);
						stringBuffer.append(genFeature.getUpperName());
						stringBuffer.append(TEXT_1472);
					}
				} else if (genFeature.isListType() && !genFeature.isUnsettable()) {
					if (genFeature.isWrappedFeatureMapType()) {
						if (genFeature.isVolatile()) {
							stringBuffer.append(TEXT_1473);
							stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.FeatureMap"));
							stringBuffer.append(TEXT_1474);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1475);
						} else {
							stringBuffer.append(TEXT_1476);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1477);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1478);
						}
					} else {
						if (genClass.isField(genFeature)) {
							stringBuffer.append(TEXT_1479);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1480);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1481);
						} else {
							if (genFeature.isField()
									&& genClass.getImplementingGenModel(genFeature).isVirtualDelegation()) {
								stringBuffer.append(TEXT_1482);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_1483);
								stringBuffer.append(safeNameAccessor);
								stringBuffer.append(TEXT_1484);
								stringBuffer.append(genFeature.getImportedType(genClass));
								stringBuffer.append(TEXT_1485);
								stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
								stringBuffer.append(positiveOffsetCorrection);
								stringBuffer.append(TEXT_1486);
								stringBuffer.append(safeNameAccessor);
								stringBuffer.append(TEXT_1487);
								stringBuffer.append(safeNameAccessor);
								stringBuffer.append(TEXT_1488);
							} else {
								stringBuffer.append(TEXT_1489);
								stringBuffer.append(genFeature.getGetAccessor());
								stringBuffer.append(TEXT_1490);
							}
						}
					}
				} else if (genFeature.isUnsettable()) {
					stringBuffer.append(TEXT_1491);
					stringBuffer.append(genFeature.getAccessorName());
					stringBuffer.append(TEXT_1492);
				} else if (genFeature.isResolveProxies()) {
					if (genClass.isField(genFeature)) {
						stringBuffer.append(TEXT_1493);
						stringBuffer.append(safeNameAccessor);
						stringBuffer.append(TEXT_1494);
					} else {
						if (genFeature.isField()
								&& genClass.getImplementingGenModel(genFeature).isVirtualDelegation()) {
							stringBuffer.append(TEXT_1495);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_1496);
						} else {
							stringBuffer.append(TEXT_1497);
							stringBuffer.append(genFeature.getAccessorName());
							stringBuffer.append(TEXT_1498);
						}
					}
				} else if (!genFeature.hasEDefault()) {
					if (genClass.isField(genFeature)) {
						stringBuffer.append(TEXT_1499);
						stringBuffer.append(safeNameAccessor);
						stringBuffer.append(TEXT_1500);
					} else {
						if (genFeature.isField()
								&& genClass.getImplementingGenModel(genFeature).isVirtualDelegation()) {
							stringBuffer.append(TEXT_1501);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_1502);
						} else {
							stringBuffer.append(TEXT_1503);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1504);
						}
					}
				} else if (genFeature.isPrimitiveType() || genFeature.isEnumType()) {
					if (genClass.isField(genFeature)) {
						if (genClass.isFlag(genFeature)) {
							if (genFeature.isBooleanType()) {
								stringBuffer.append(TEXT_1505);
								stringBuffer.append(genClass.getFlagsField(genFeature));
								stringBuffer.append(TEXT_1506);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_1507);
								stringBuffer.append(genFeature.getEDefault());
								stringBuffer.append(TEXT_1508);
							} else {
								stringBuffer.append(TEXT_1509);
								stringBuffer.append(genClass.getFlagsField(genFeature));
								stringBuffer.append(TEXT_1510);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_1511);
								stringBuffer.append(genFeature.getUpperName());
								stringBuffer.append(TEXT_1512);
							}
						} else {
							stringBuffer.append(TEXT_1513);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1514);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1515);
						}
					} else {
						if (genFeature.isEnumType() && genFeature.isField()
								&& genClass.getImplementingGenModel(genFeature).isVirtualDelegation()) {
							stringBuffer.append(TEXT_1516);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_1517);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1518);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1519);
						} else {
							stringBuffer.append(TEXT_1520);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1521);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1522);
						}
					}
				} else {// datatype
					if (genClass.isField(genFeature)) {
						stringBuffer.append(TEXT_1523);
						stringBuffer.append(genFeature.getEDefault());
						stringBuffer.append(TEXT_1524);
						stringBuffer.append(safeNameAccessor);
						stringBuffer.append(TEXT_1525);
						stringBuffer.append(genFeature.getEDefault());
						stringBuffer.append(TEXT_1526);
						stringBuffer.append(safeNameAccessor);
						stringBuffer.append(TEXT_1527);
					} else {
						if (genFeature.isField()
								&& genClass.getImplementingGenModel(genFeature).isVirtualDelegation()) {
							stringBuffer.append(TEXT_1528);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_1529);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1530);
							stringBuffer.append(genFeature.getImportedType(genClass));
							stringBuffer.append(TEXT_1531);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_1532);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1533);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1534);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1535);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1536);
							stringBuffer.append(safeNameAccessor);
							stringBuffer.append(TEXT_1537);
						} else {
							stringBuffer.append(TEXT_1538);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1539);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1540);
							stringBuffer.append(genFeature.getEDefault());
							stringBuffer.append(TEXT_1541);
							stringBuffer.append(genFeature.getGetAccessor());
							stringBuffer.append(TEXT_1542);
						}
					}
				}
			}
			stringBuffer.append(TEXT_1543);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1544);
			} else {
				stringBuffer.append(TEXT_1545);
			}
			stringBuffer.append(TEXT_1546);
			// Class/eIsSet.override.javajetinc
		}
		if (isImplementation && (!genClass.getMixinGenFeatures().isEmpty()
				|| genClass.hasOffsetCorrection() && !genClass.getGenFeatures().isEmpty())) {
			if (!genClass.getMixinGenFeatures().isEmpty()) {
				stringBuffer.append(TEXT_1547);
				if (genModel.useClassOverrideAnnotation()) {
					stringBuffer.append(TEXT_1548);
				}
				stringBuffer.append(TEXT_1549);
				stringBuffer.append(singleWildcard);
				stringBuffer.append(TEXT_1550);
				for (GenClass mixinGenClass : genClass.getMixinGenClasses()) {
					stringBuffer.append(TEXT_1551);
					stringBuffer.append(mixinGenClass.getRawImportedInterfaceName());
					stringBuffer.append(TEXT_1552);
					stringBuffer.append(negativeOffsetCorrection);
					stringBuffer.append(TEXT_1553);
					for (GenFeature genFeature : mixinGenClass.getGenFeatures()) {
						stringBuffer.append(TEXT_1554);
						stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
						stringBuffer.append(TEXT_1555);
						stringBuffer.append(mixinGenClass.getQualifiedFeatureID(genFeature));
						stringBuffer.append(TEXT_1556);
					}
					stringBuffer.append(TEXT_1557);
				}
				stringBuffer.append(TEXT_1558);
			}
			stringBuffer.append(TEXT_1559);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1560);
			}
			stringBuffer.append(TEXT_1561);
			stringBuffer.append(singleWildcard);
			stringBuffer.append(TEXT_1562);
			for (GenClass mixinGenClass : genClass.getMixinGenClasses()) {
				stringBuffer.append(TEXT_1563);
				stringBuffer.append(mixinGenClass.getRawImportedInterfaceName());
				stringBuffer.append(TEXT_1564);
				for (GenFeature genFeature : mixinGenClass.getGenFeatures()) {
					stringBuffer.append(TEXT_1565);
					stringBuffer.append(mixinGenClass.getQualifiedFeatureID(genFeature));
					stringBuffer.append(TEXT_1566);
					stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
					stringBuffer.append(positiveOffsetCorrection);
					stringBuffer.append(TEXT_1567);
				}
				stringBuffer.append(TEXT_1568);
			}
			if (genClass.hasOffsetCorrection() && !genClass.getGenFeatures().isEmpty()) {
				stringBuffer.append(TEXT_1569);
				stringBuffer.append(genClass.getRawImportedInterfaceName());
				stringBuffer.append(TEXT_1570);
				stringBuffer.append(negativeOffsetCorrection);
				stringBuffer.append(TEXT_1571);
				for (GenFeature genFeature : genClass.getGenFeatures()) {
					stringBuffer.append(TEXT_1572);
					stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
					stringBuffer.append(TEXT_1573);
					stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
					stringBuffer.append(positiveOffsetCorrection);
					stringBuffer.append(TEXT_1574);
				}
				stringBuffer.append(TEXT_1575);
			}
			stringBuffer.append(TEXT_1576);
		}
		if (genModel.isOperationReflection() && isImplementation
				&& (!genClass.getMixinGenOperations().isEmpty()
						|| !genClass.getOverrideGenOperations(genClass.getExtendedGenOperations(),
								genClass.getImplementedGenOperations()).isEmpty()
						|| genClass.hasOffsetCorrection() && !genClass.getGenOperations().isEmpty())) {
			stringBuffer.append(TEXT_1577);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1578);
			}
			stringBuffer.append(TEXT_1579);
			stringBuffer.append(singleWildcard);
			stringBuffer.append(TEXT_1580);
			for (GenClass extendedGenClass : genClass.getExtendedGenClasses()) {
				List<GenOperation> extendedImplementedGenOperations = extendedGenClass.getImplementedGenOperations();
				List<GenOperation> implementedGenOperations = genClass.getImplementedGenOperations();
				if (!genClass.getOverrideGenOperations(extendedImplementedGenOperations, implementedGenOperations)
						.isEmpty()) {
					stringBuffer.append(TEXT_1581);
					stringBuffer.append(extendedGenClass.getRawImportedInterfaceName());
					stringBuffer.append(TEXT_1582);
					for (GenOperation genOperation : extendedImplementedGenOperations) {
						GenOperation overrideGenOperation = genClass.getOverrideGenOperation(genOperation);
						if (implementedGenOperations.contains(overrideGenOperation)) {
							stringBuffer.append(TEXT_1583);
							stringBuffer.append(extendedGenClass.getQualifiedOperationID(genOperation));
							stringBuffer.append(TEXT_1584);
							stringBuffer.append(genClass.getQualifiedOperationID(overrideGenOperation));
							stringBuffer.append(positiveOperationOffsetCorrection);
							stringBuffer.append(TEXT_1585);
						}
					}
					stringBuffer.append(TEXT_1586);
				}
			}
			for (GenClass mixinGenClass : genClass.getMixinGenClasses()) {
				stringBuffer.append(TEXT_1587);
				stringBuffer.append(mixinGenClass.getRawImportedInterfaceName());
				stringBuffer.append(TEXT_1588);
				for (GenOperation genOperation : mixinGenClass.getGenOperations()) {
					GenOperation overrideGenOperation = genClass.getOverrideGenOperation(genOperation);
					stringBuffer.append(TEXT_1589);
					stringBuffer.append(mixinGenClass.getQualifiedOperationID(genOperation));
					stringBuffer.append(TEXT_1590);
					stringBuffer.append(genClass.getQualifiedOperationID(
							overrideGenOperation != null ? overrideGenOperation : genOperation));
					stringBuffer.append(positiveOperationOffsetCorrection);
					stringBuffer.append(TEXT_1591);
				}
				stringBuffer.append(TEXT_1592);
			}
			if (genClass.hasOffsetCorrection() && !genClass.getGenOperations().isEmpty()) {
				stringBuffer.append(TEXT_1593);
				stringBuffer.append(genClass.getRawImportedInterfaceName());
				stringBuffer.append(TEXT_1594);
				stringBuffer.append(negativeOperationOffsetCorrection);
				stringBuffer.append(TEXT_1595);
				for (GenOperation genOperation : genClass.getGenOperations()) {
					stringBuffer.append(TEXT_1596);
					stringBuffer.append(genClass.getQualifiedOperationID(genOperation));
					stringBuffer.append(TEXT_1597);
					stringBuffer.append(genClass.getQualifiedOperationID(genOperation));
					stringBuffer.append(positiveOperationOffsetCorrection);
					stringBuffer.append(TEXT_1598);
				}
				stringBuffer.append(TEXT_1599);
			}
			stringBuffer.append(TEXT_1600);
		}
		if (isImplementation && genModel.isVirtualDelegation()) {
			String eVirtualValuesField = genClass.getEVirtualValuesField();
			if (eVirtualValuesField != null) {
				stringBuffer.append(TEXT_1601);
				if (genModel.useClassOverrideAnnotation()) {
					stringBuffer.append(TEXT_1602);
				}
				stringBuffer.append(TEXT_1603);
				stringBuffer.append(eVirtualValuesField);
				stringBuffer.append(TEXT_1604);
				if (genModel.useClassOverrideAnnotation()) {
					stringBuffer.append(TEXT_1605);
				}
				stringBuffer.append(TEXT_1606);
				stringBuffer.append(eVirtualValuesField);
				stringBuffer.append(TEXT_1607);
			}
			{
				List<String> eVirtualIndexBitFields = genClass.getEVirtualIndexBitFields(new ArrayList<String>());
				if (!eVirtualIndexBitFields.isEmpty()) {
					List<String> allEVirtualIndexBitFields = genClass
							.getAllEVirtualIndexBitFields(new ArrayList<String>());
					stringBuffer.append(TEXT_1608);
					if (genModel.useClassOverrideAnnotation()) {
						stringBuffer.append(TEXT_1609);
					}
					stringBuffer.append(TEXT_1610);
					for (int i = 0; i < allEVirtualIndexBitFields.size(); i++) {
						stringBuffer.append(TEXT_1611);
						stringBuffer.append(i);
						stringBuffer.append(TEXT_1612);
						stringBuffer.append(allEVirtualIndexBitFields.get(i));
						stringBuffer.append(TEXT_1613);
					}
					stringBuffer.append(TEXT_1614);
					if (genModel.useClassOverrideAnnotation()) {
						stringBuffer.append(TEXT_1615);
					}
					stringBuffer.append(TEXT_1616);
					for (int i = 0; i < allEVirtualIndexBitFields.size(); i++) {
						stringBuffer.append(TEXT_1617);
						stringBuffer.append(i);
						stringBuffer.append(TEXT_1618);
						stringBuffer.append(allEVirtualIndexBitFields.get(i));
						stringBuffer.append(TEXT_1619);
					}
					stringBuffer.append(TEXT_1620);
				}
			}
		}
		if (genModel.isOperationReflection() && isImplementation && !genClass.getImplementedGenOperations().isEmpty()) {
			stringBuffer.append(TEXT_1621);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1622);
			}
			if (genModel.useGenerics()) {
				boolean isUnchecked = false;
				boolean isRaw = false;
				LOOP: for (GenOperation genOperation : (genModel.isMinimalReflectiveMethods()
						? genClass.getImplementedGenOperations()
						: genClass.getAllGenOperations())) {
					for (GenParameter genParameter : genOperation.getGenParameters()) {
						if (genParameter.isUncheckedCast()) {
							if (genParameter.getTypeGenDataType() == null
									|| !genParameter.getTypeGenDataType().isObjectType()) {
								isUnchecked = true;
							}
							if (genParameter.usesOperationTypeParameters() && !genParameter.getEcoreParameter()
									.getEGenericType().getETypeArguments().isEmpty()) {
								isRaw = true;
								break LOOP;
							}
						}
					}
				}
				if (isUnchecked) {
					stringBuffer.append(TEXT_1623);
					if (!isRaw) {
						stringBuffer.append(TEXT_1624);
					} else {
						stringBuffer.append(TEXT_1625);
					}
					stringBuffer.append(TEXT_1626);
				}
			}
			stringBuffer.append(TEXT_1627);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.common.util.EList"));
			stringBuffer.append(singleWildcard);
			stringBuffer.append(TEXT_1628);
			stringBuffer.append(genModel.getImportedName(isGWT ? "org.eclipse.emf.common.util.InvocationTargetException"
					: "java.lang.reflect.InvocationTargetException"));
			stringBuffer.append(TEXT_1629);
			stringBuffer.append(negativeOperationOffsetCorrection);
			stringBuffer.append(TEXT_1630);
			for (GenOperation genOperation : (genModel.isMinimalReflectiveMethods()
					? genClass.getImplementedGenOperations()
					: genClass.getAllGenOperations())) {
				List<GenParameter> genParameters = genOperation.getGenParameters();
				int size = genParameters.size();
				stringBuffer.append(TEXT_1631);
				stringBuffer.append(genClass.getQualifiedOperationID(genOperation));
				stringBuffer.append(TEXT_1632);
				if (genOperation.isVoid()) {
					stringBuffer.append(TEXT_1633);
					stringBuffer.append(genOperation.getName());
					stringBuffer.append(TEXT_1634);
					for (int i = 0; i < size; i++) {
						GenParameter genParameter = genParameters.get(i);
						if (!isJDK50 && genParameter.isPrimitiveType()) {
							stringBuffer.append(TEXT_1635);
						}
						if (genParameter.getTypeGenDataType() == null
								|| !genParameter.getTypeGenDataType().isObjectType()
								|| !genParameter.usesOperationTypeParameters()
										&& !genParameter.getRawType().equals(genParameter.getType(genClass))) {
							stringBuffer.append(TEXT_1636);
							stringBuffer.append(
									genParameter.usesOperationTypeParameters() ? genParameter.getRawImportedType()
											: genParameter.getObjectType(genClass));
							stringBuffer.append(TEXT_1637);
						}
						stringBuffer.append(TEXT_1638);
						stringBuffer.append(i);
						stringBuffer.append(TEXT_1639);
						if (!isJDK50 && genParameter.isPrimitiveType()) {
							stringBuffer.append(TEXT_1640);
							stringBuffer.append(genParameter.getPrimitiveValueFunction());
							stringBuffer.append(TEXT_1641);
						}
						if (i < (size - 1)) {
							stringBuffer.append(TEXT_1642);
						}
					}
					stringBuffer.append(TEXT_1643);
				} else {
					stringBuffer.append(TEXT_1644);
					if (!isJDK50 && genOperation.isPrimitiveType()) {
						stringBuffer.append(TEXT_1645);
						stringBuffer.append(genOperation.getObjectType(genClass));
						stringBuffer.append(TEXT_1646);
					}
					stringBuffer.append(genOperation.getName());
					stringBuffer.append(TEXT_1647);
					for (int i = 0; i < size; i++) {
						GenParameter genParameter = genParameters.get(i);
						if (!isJDK50 && genParameter.isPrimitiveType()) {
							stringBuffer.append(TEXT_1648);
						}
						if (genParameter.getTypeGenDataType() == null
								|| !genParameter.getTypeGenDataType().isObjectType()
								|| !genParameter.usesOperationTypeParameters()
										&& !genParameter.getRawType().equals(genParameter.getType(genClass))) {
							stringBuffer.append(TEXT_1649);
							stringBuffer.append(
									genParameter.usesOperationTypeParameters() ? genParameter.getRawImportedType()
											: genParameter.getObjectType(genClass));
							stringBuffer.append(TEXT_1650);
						}
						stringBuffer.append(TEXT_1651);
						stringBuffer.append(i);
						stringBuffer.append(TEXT_1652);
						if (!isJDK50 && genParameter.isPrimitiveType()) {
							stringBuffer.append(TEXT_1653);
							stringBuffer.append(genParameter.getPrimitiveValueFunction());
							stringBuffer.append(TEXT_1654);
						}
						if (i < (size - 1)) {
							stringBuffer.append(TEXT_1655);
						}
					}
					stringBuffer.append(TEXT_1656);
					if (!isJDK50 && genOperation.isPrimitiveType()) {
						stringBuffer.append(TEXT_1657);
					}
					stringBuffer.append(TEXT_1658);
				}
			}
			stringBuffer.append(TEXT_1659);
			if (genModel.isMinimalReflectiveMethods()) {
				stringBuffer.append(TEXT_1660);
			} else {
				stringBuffer.append(TEXT_1661);
			}
			stringBuffer.append(TEXT_1662);
		}
		if (!genClass.hasImplementedToStringGenOperation() && isImplementation && !genModel.isReflectiveDelegation()
				&& !genModel.isDynamicDelegation() && !genClass.getToStringGenFeatures().isEmpty()) {
			stringBuffer.append(TEXT_1663);
			if (genModel.useClassOverrideAnnotation()) {
				stringBuffer.append(TEXT_1664);
			}
			stringBuffer.append(TEXT_1665);
			{
				boolean first = true;
				for (GenFeature genFeature : genClass.getToStringGenFeatures()) {
					if (first) {
						first = false;
						stringBuffer.append(TEXT_1666);
						stringBuffer.append(genFeature.getName());
						stringBuffer.append(TEXT_1667);
						stringBuffer.append(genModel.getNonNLS());
					} else {
						stringBuffer.append(TEXT_1668);
						stringBuffer.append(genFeature.getName());
						stringBuffer.append(TEXT_1669);
						stringBuffer.append(genModel.getNonNLS());
					}
					if (genFeature.isUnsettable() && !genFeature.isListType()) {
						if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
							stringBuffer.append(TEXT_1670);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_1671);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							stringBuffer.append(TEXT_1672);
							stringBuffer.append(genModel.getNonNLS());
						} else {
							if (genClass.isFlag(genFeature)) {
								if (genFeature.isBooleanType()) {
									stringBuffer.append(TEXT_1673);
									if (genClass.isESetFlag(genFeature)) {
										stringBuffer.append(TEXT_1674);
										stringBuffer.append(genClass.getESetFlagsField(genFeature));
										stringBuffer.append(TEXT_1675);
										stringBuffer.append(genFeature.getUpperName());
										stringBuffer.append(TEXT_1676);
									} else {
										stringBuffer.append(genFeature.getUncapName());
										stringBuffer.append(TEXT_1677);
									}
									stringBuffer.append(TEXT_1678);
									stringBuffer.append(genClass.getFlagsField(genFeature));
									stringBuffer.append(TEXT_1679);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1680);
									stringBuffer.append(genModel.getNonNLS());
								} else {
									stringBuffer.append(TEXT_1681);
									if (genClass.isESetFlag(genFeature)) {
										stringBuffer.append(TEXT_1682);
										stringBuffer.append(genClass.getESetFlagsField(genFeature));
										stringBuffer.append(TEXT_1683);
										stringBuffer.append(genFeature.getUpperName());
										stringBuffer.append(TEXT_1684);
									} else {
										stringBuffer.append(genFeature.getUncapName());
										stringBuffer.append(TEXT_1685);
									}
									stringBuffer.append(TEXT_1686);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1687);
									stringBuffer.append(genClass.getFlagsField(genFeature));
									stringBuffer.append(TEXT_1688);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1689);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1690);
									stringBuffer.append(genModel.getNonNLS());
								}
							} else {
								stringBuffer.append(TEXT_1691);
								if (genClass.isESetFlag(genFeature)) {
									stringBuffer.append(TEXT_1692);
									stringBuffer.append(genClass.getESetFlagsField(genFeature));
									stringBuffer.append(TEXT_1693);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1694);
								} else {
									stringBuffer.append(genFeature.getUncapName());
									stringBuffer.append(TEXT_1695);
								}
								stringBuffer.append(TEXT_1696);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_1697);
								stringBuffer.append(genModel.getNonNLS());
							}
						}
					} else {
						if (genModel.isVirtualDelegation() && !genFeature.isPrimitiveType()) {
							stringBuffer.append(TEXT_1698);
							stringBuffer.append(genClass.getQualifiedFeatureID(genFeature));
							stringBuffer.append(positiveOffsetCorrection);
							if (!genFeature.isListType() && !genFeature.isReferenceType()) {
								stringBuffer.append(TEXT_1699);
								stringBuffer.append(genFeature.getEDefault());
							}
							stringBuffer.append(TEXT_1700);
						} else {
							if (genClass.isFlag(genFeature)) {
								if (genFeature.isBooleanType()) {
									stringBuffer.append(TEXT_1701);
									stringBuffer.append(genClass.getFlagsField(genFeature));
									stringBuffer.append(TEXT_1702);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1703);
								} else {
									stringBuffer.append(TEXT_1704);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1705);
									stringBuffer.append(genClass.getFlagsField(genFeature));
									stringBuffer.append(TEXT_1706);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1707);
									stringBuffer.append(genFeature.getUpperName());
									stringBuffer.append(TEXT_1708);
								}
							} else {
								stringBuffer.append(TEXT_1709);
								stringBuffer.append(genFeature.getSafeName());
								stringBuffer.append(TEXT_1710);
							}
						}
					}
				}
			}
			stringBuffer.append(TEXT_1711);
		}
		if (isImplementation && genClass.isMapEntry()) {
			GenFeature keyFeature = genClass.getMapEntryKeyFeature();
			GenFeature valueFeature = genClass.getMapEntryValueFeature();
			String objectType = genModel.getImportedName("java.lang.Object");
			String keyType = isJDK50 ? keyFeature.getObjectType(genClass) : objectType;
			String valueType = isJDK50 ? valueFeature.getObjectType(genClass) : objectType;
			String eMapType = genModel.getImportedName("org.eclipse.emf.common.util.EMap")
					+ (isJDK50 ? "<" + keyType + ", " + valueType + ">" : "");
			stringBuffer.append(TEXT_1712);
			if (isGWT) {
				stringBuffer.append(TEXT_1713);
				stringBuffer.append(genModel.getImportedName("com.google.gwt.user.client.rpc.GwtTransient"));
			}
			stringBuffer.append(TEXT_1714);
			stringBuffer.append(objectType);
			stringBuffer.append(TEXT_1715);
			stringBuffer.append(keyType);
			stringBuffer.append(TEXT_1716);
			if (!isJDK50 && keyFeature.isPrimitiveType()) {
				stringBuffer.append(TEXT_1717);
				stringBuffer.append(keyFeature.getObjectType(genClass));
				stringBuffer.append(TEXT_1718);
			} else {
				stringBuffer.append(TEXT_1719);
			}
			stringBuffer.append(TEXT_1720);
			stringBuffer.append(keyType);
			stringBuffer.append(TEXT_1721);
			if (keyFeature.isListType()) {
				stringBuffer.append(TEXT_1722);
				if (!genModel.useGenerics()) {
					stringBuffer.append(TEXT_1723);
					stringBuffer.append(genModel.getImportedName("java.util.Collection"));
					stringBuffer.append(TEXT_1724);
				}
				stringBuffer.append(TEXT_1725);
			} else if (isJDK50) {
				stringBuffer.append(TEXT_1726);
			} else if (keyFeature.isPrimitiveType()) {
				stringBuffer.append(TEXT_1727);
				stringBuffer.append(keyFeature.getObjectType(genClass));
				stringBuffer.append(TEXT_1728);
				stringBuffer.append(keyFeature.getPrimitiveValueFunction());
				stringBuffer.append(TEXT_1729);
			} else {
				stringBuffer.append(TEXT_1730);
				stringBuffer.append(keyFeature.getImportedType(genClass));
				stringBuffer.append(TEXT_1731);
			}
			stringBuffer.append(TEXT_1732);
			stringBuffer.append(valueType);
			stringBuffer.append(TEXT_1733);
			if (!isJDK50 && valueFeature.isPrimitiveType()) {
				stringBuffer.append(TEXT_1734);
				stringBuffer.append(valueFeature.getObjectType(genClass));
				stringBuffer.append(TEXT_1735);
			} else {
				stringBuffer.append(TEXT_1736);
			}
			stringBuffer.append(TEXT_1737);
			stringBuffer.append(valueType);
			stringBuffer.append(TEXT_1738);
			stringBuffer.append(valueType);
			stringBuffer.append(TEXT_1739);
			stringBuffer.append(valueType);
			stringBuffer.append(TEXT_1740);
			if (valueFeature.isListType()) {
				stringBuffer.append(TEXT_1741);
				if (!genModel.useGenerics()) {
					stringBuffer.append(TEXT_1742);
					stringBuffer.append(genModel.getImportedName("java.util.Collection"));
					stringBuffer.append(TEXT_1743);
				}
				stringBuffer.append(TEXT_1744);
			} else if (isJDK50) {
				stringBuffer.append(TEXT_1745);
			} else if (valueFeature.isPrimitiveType()) {
				stringBuffer.append(TEXT_1746);
				stringBuffer.append(valueFeature.getObjectType(genClass));
				stringBuffer.append(TEXT_1747);
				stringBuffer.append(valueFeature.getPrimitiveValueFunction());
				stringBuffer.append(TEXT_1748);
			} else {
				stringBuffer.append(TEXT_1749);
				stringBuffer.append(valueFeature.getImportedType(genClass));
				stringBuffer.append(TEXT_1750);
			}
			stringBuffer.append(TEXT_1751);
			if (genModel.useGenerics()) {
				stringBuffer.append(TEXT_1752);
			}
			stringBuffer.append(TEXT_1753);
			stringBuffer.append(eMapType);
			stringBuffer.append(TEXT_1754);
			stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EObject"));
			stringBuffer.append(TEXT_1755);
			stringBuffer.append(eMapType);
			stringBuffer.append(TEXT_1756);
		}
		stringBuffer.append(TEXT_1757);
		stringBuffer.append(isInterface ? " " + genClass.getInterfaceName() : genClass.getClassName());
		genModel.emitSortedImports();
		stringBuffer.append(TEXT_1758);
		return stringBuffer.toString();
	}
}

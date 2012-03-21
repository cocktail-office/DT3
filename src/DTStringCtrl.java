/*
 * Copyright CRI - Universite de La Rochelle, 1995-2005 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use, 
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and, more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
import java.text.DecimalFormat;

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOMessage;
import com.webobjects.foundation.NSMutableDictionary;

public class DTStringCtrl extends StringCtrl {

	private static NSMutableDictionary _htmlEntities;

	private static NSMutableDictionary htmlEntities() {
		if (_htmlEntities == null) {
			_htmlEntities = new NSMutableDictionary();
			_htmlEntities.setObjectForKey("nbsp", new Integer(160));
			_htmlEntities.setObjectForKey("iexcl", new Integer(161));
			_htmlEntities.setObjectForKey("cent", new Integer(162));
			_htmlEntities.setObjectForKey("pound", new Integer(163));
			_htmlEntities.setObjectForKey("curren", new Integer(164));
			_htmlEntities.setObjectForKey("yen", new Integer(165));
			_htmlEntities.setObjectForKey("brvbar", new Integer(166));
			_htmlEntities.setObjectForKey("sect", new Integer(167));
			_htmlEntities.setObjectForKey("uml", new Integer(168));
			_htmlEntities.setObjectForKey("copy", new Integer(169));
			_htmlEntities.setObjectForKey("ordf", new Integer(170));
			_htmlEntities.setObjectForKey("laquo", new Integer(171));
			_htmlEntities.setObjectForKey("not", new Integer(172));
			_htmlEntities.setObjectForKey("shy", new Integer(173));
			_htmlEntities.setObjectForKey("reg", new Integer(174));
			_htmlEntities.setObjectForKey("macr", new Integer(175));
			_htmlEntities.setObjectForKey("deg", new Integer(176));
			_htmlEntities.setObjectForKey("plusmn", new Integer(177));
			_htmlEntities.setObjectForKey("sup2", new Integer(178));
			_htmlEntities.setObjectForKey("sup3", new Integer(179));
			_htmlEntities.setObjectForKey("acute", new Integer(180));
			_htmlEntities.setObjectForKey("micro", new Integer(181));
			_htmlEntities.setObjectForKey("para", new Integer(182));
			_htmlEntities.setObjectForKey("middot", new Integer(183));
			_htmlEntities.setObjectForKey("cedil", new Integer(184));
			_htmlEntities.setObjectForKey("sup1", new Integer(185));
			_htmlEntities.setObjectForKey("ordm", new Integer(186));
			_htmlEntities.setObjectForKey("raquo", new Integer(187));
			_htmlEntities.setObjectForKey("frac14", new Integer(188));
			_htmlEntities.setObjectForKey("frac12", new Integer(189));
			_htmlEntities.setObjectForKey("frac34", new Integer(190));
			_htmlEntities.setObjectForKey("iquest", new Integer(191));
			_htmlEntities.setObjectForKey("Agrave", new Integer(192));
			_htmlEntities.setObjectForKey("Aacute", new Integer(193));
			_htmlEntities.setObjectForKey("Acirc", new Integer(194));
			_htmlEntities.setObjectForKey("Atilde", new Integer(195));
			_htmlEntities.setObjectForKey("Auml", new Integer(196));
			_htmlEntities.setObjectForKey("Aring", new Integer(197));
			_htmlEntities.setObjectForKey("AElig", new Integer(198));
			_htmlEntities.setObjectForKey("Ccedil", new Integer(199));
			_htmlEntities.setObjectForKey("Egrave", new Integer(200));
			_htmlEntities.setObjectForKey("Eacute", new Integer(201));
			_htmlEntities.setObjectForKey("Ecirc", new Integer(202));
			_htmlEntities.setObjectForKey("Euml", new Integer(203));
			_htmlEntities.setObjectForKey("Igrave", new Integer(204));
			_htmlEntities.setObjectForKey("Iacute", new Integer(205));
			_htmlEntities.setObjectForKey("Icirc", new Integer(206));
			_htmlEntities.setObjectForKey("Iuml", new Integer(207));
			_htmlEntities.setObjectForKey("ETH", new Integer(208));
			_htmlEntities.setObjectForKey("Ntilde", new Integer(209));
			_htmlEntities.setObjectForKey("Ograve", new Integer(210));
			_htmlEntities.setObjectForKey("Oacute", new Integer(211));
			_htmlEntities.setObjectForKey("Ocirc", new Integer(212));
			_htmlEntities.setObjectForKey("Otilde", new Integer(213));
			_htmlEntities.setObjectForKey("Ouml", new Integer(214));
			_htmlEntities.setObjectForKey("times", new Integer(215));
			_htmlEntities.setObjectForKey("Oslash", new Integer(216));
			_htmlEntities.setObjectForKey("Ugrave", new Integer(217));
			_htmlEntities.setObjectForKey("Uacute", new Integer(218));
			_htmlEntities.setObjectForKey("Ucirc", new Integer(219));
			_htmlEntities.setObjectForKey("Uuml", new Integer(220));
			_htmlEntities.setObjectForKey("Yacute", new Integer(221));
			_htmlEntities.setObjectForKey("THORN", new Integer(222));
			_htmlEntities.setObjectForKey("szlig", new Integer(223));
			_htmlEntities.setObjectForKey("agrave", new Integer(224));
			_htmlEntities.setObjectForKey("aacute", new Integer(225));
			_htmlEntities.setObjectForKey("acirc", new Integer(226));
			_htmlEntities.setObjectForKey("atilde", new Integer(227));
			_htmlEntities.setObjectForKey("auml", new Integer(228));
			_htmlEntities.setObjectForKey("aring", new Integer(229));
			_htmlEntities.setObjectForKey("aelig", new Integer(230));
			_htmlEntities.setObjectForKey("ccedil", new Integer(231));
			_htmlEntities.setObjectForKey("egrave", new Integer(232));
			_htmlEntities.setObjectForKey("eacute", new Integer(233));
			_htmlEntities.setObjectForKey("ecirc", new Integer(234));
			_htmlEntities.setObjectForKey("euml", new Integer(235));
			_htmlEntities.setObjectForKey("igrave", new Integer(236));
			_htmlEntities.setObjectForKey("iacute", new Integer(237));
			_htmlEntities.setObjectForKey("icirc", new Integer(238));
			_htmlEntities.setObjectForKey("iuml", new Integer(239));
			_htmlEntities.setObjectForKey("eth", new Integer(240));
			_htmlEntities.setObjectForKey("ntilde", new Integer(241));
			_htmlEntities.setObjectForKey("ograve", new Integer(242));
			_htmlEntities.setObjectForKey("oacute", new Integer(243));
			_htmlEntities.setObjectForKey("ocirc", new Integer(244));
			_htmlEntities.setObjectForKey("otilde", new Integer(245));
			_htmlEntities.setObjectForKey("ouml", new Integer(246));
			_htmlEntities.setObjectForKey("divide", new Integer(247));
			_htmlEntities.setObjectForKey("oslash", new Integer(248));
			_htmlEntities.setObjectForKey("ugrave", new Integer(249));
			_htmlEntities.setObjectForKey("uacute", new Integer(250));
			_htmlEntities.setObjectForKey("ucirc", new Integer(251));
			_htmlEntities.setObjectForKey("uuml", new Integer(252));
			_htmlEntities.setObjectForKey("yacute", new Integer(253));
			_htmlEntities.setObjectForKey("thorn", new Integer(254));
			_htmlEntities.setObjectForKey("yuml", new Integer(255));
			_htmlEntities.setObjectForKey("fnof", new Integer(402));
			_htmlEntities.setObjectForKey("Alpha", new Integer(913));
			_htmlEntities.setObjectForKey("Beta", new Integer(914));
			_htmlEntities.setObjectForKey("Gamma", new Integer(915));
			_htmlEntities.setObjectForKey("Delta", new Integer(916));
			_htmlEntities.setObjectForKey("Epsilon", new Integer(917));
			_htmlEntities.setObjectForKey("Zeta", new Integer(918));
			_htmlEntities.setObjectForKey("Eta", new Integer(919));
			_htmlEntities.setObjectForKey("Theta", new Integer(920));
			_htmlEntities.setObjectForKey("Iota", new Integer(921));
			_htmlEntities.setObjectForKey("Kappa", new Integer(922));
			_htmlEntities.setObjectForKey("Lambda", new Integer(923));
			_htmlEntities.setObjectForKey("Mu", new Integer(924));
			_htmlEntities.setObjectForKey("Nu", new Integer(925));
			_htmlEntities.setObjectForKey("Xi", new Integer(926));
			_htmlEntities.setObjectForKey("Omicron", new Integer(927));
			_htmlEntities.setObjectForKey("Pi", new Integer(928));
			_htmlEntities.setObjectForKey("Rho", new Integer(929));
			_htmlEntities.setObjectForKey("Sigma", new Integer(931));
			_htmlEntities.setObjectForKey("Tau", new Integer(932));
			_htmlEntities.setObjectForKey("Upsilon", new Integer(933));
			_htmlEntities.setObjectForKey("Phi", new Integer(934));
			_htmlEntities.setObjectForKey("Chi", new Integer(935));
			_htmlEntities.setObjectForKey("Psi", new Integer(936));
			_htmlEntities.setObjectForKey("Omega", new Integer(937));
			_htmlEntities.setObjectForKey("alpha", new Integer(945));
			_htmlEntities.setObjectForKey("beta", new Integer(946));
			_htmlEntities.setObjectForKey("gamma", new Integer(947));
			_htmlEntities.setObjectForKey("delta", new Integer(948));
			_htmlEntities.setObjectForKey("epsilon", new Integer(949));
			_htmlEntities.setObjectForKey("zeta", new Integer(950));
			_htmlEntities.setObjectForKey("eta", new Integer(951));
			_htmlEntities.setObjectForKey("theta", new Integer(952));
			_htmlEntities.setObjectForKey("iota", new Integer(953));
			_htmlEntities.setObjectForKey("kappa", new Integer(954));
			_htmlEntities.setObjectForKey("lambda", new Integer(955));
			_htmlEntities.setObjectForKey("mu", new Integer(956));
			_htmlEntities.setObjectForKey("nu", new Integer(957));
			_htmlEntities.setObjectForKey("xi", new Integer(958));
			_htmlEntities.setObjectForKey("omicron", new Integer(959));
			_htmlEntities.setObjectForKey("pi", new Integer(960));
			_htmlEntities.setObjectForKey("rho", new Integer(961));
			_htmlEntities.setObjectForKey("sigmaf", new Integer(962));
			_htmlEntities.setObjectForKey("sigma", new Integer(963));
			_htmlEntities.setObjectForKey("tau", new Integer(964));
			_htmlEntities.setObjectForKey("upsilon", new Integer(965));
			_htmlEntities.setObjectForKey("phi", new Integer(966));
			_htmlEntities.setObjectForKey("chi", new Integer(967));
			_htmlEntities.setObjectForKey("psi", new Integer(968));
			_htmlEntities.setObjectForKey("omega", new Integer(969));
			_htmlEntities.setObjectForKey("thetasym", new Integer(977));
			_htmlEntities.setObjectForKey("upsih", new Integer(978));
			_htmlEntities.setObjectForKey("piv", new Integer(982));
			_htmlEntities.setObjectForKey("bull", new Integer(8226));
			_htmlEntities.setObjectForKey("hellip", new Integer(8230));
			_htmlEntities.setObjectForKey("prime", new Integer(8242));
			_htmlEntities.setObjectForKey("Prime", new Integer(8243));
			_htmlEntities.setObjectForKey("oline", new Integer(8254));
			_htmlEntities.setObjectForKey("frasl", new Integer(8260));
			_htmlEntities.setObjectForKey("weierp", new Integer(8472));
			_htmlEntities.setObjectForKey("image", new Integer(8465));
			_htmlEntities.setObjectForKey("real", new Integer(8476));
			_htmlEntities.setObjectForKey("trade", new Integer(8482));
			_htmlEntities.setObjectForKey("alefsym", new Integer(8501));
			_htmlEntities.setObjectForKey("larr", new Integer(8592));
			_htmlEntities.setObjectForKey("uarr", new Integer(8593));
			_htmlEntities.setObjectForKey("rarr", new Integer(8594));
			_htmlEntities.setObjectForKey("darr", new Integer(8595));
			_htmlEntities.setObjectForKey("harr", new Integer(8596));
			_htmlEntities.setObjectForKey("crarr", new Integer(8629));
			_htmlEntities.setObjectForKey("lArr", new Integer(8656));
			_htmlEntities.setObjectForKey("uArr", new Integer(8657));
			_htmlEntities.setObjectForKey("rArr", new Integer(8658));
			_htmlEntities.setObjectForKey("dArr", new Integer(8659));
			_htmlEntities.setObjectForKey("hArr", new Integer(8660));
			_htmlEntities.setObjectForKey("forall", new Integer(8704));
			_htmlEntities.setObjectForKey("part", new Integer(8706));
			_htmlEntities.setObjectForKey("exist", new Integer(8707));
			_htmlEntities.setObjectForKey("empty", new Integer(8709));
			_htmlEntities.setObjectForKey("nabla", new Integer(8711));
			_htmlEntities.setObjectForKey("isin", new Integer(8712));
			_htmlEntities.setObjectForKey("notin", new Integer(8713));
			_htmlEntities.setObjectForKey("ni", new Integer(8715));
			_htmlEntities.setObjectForKey("prod", new Integer(8719));
			_htmlEntities.setObjectForKey("sum", new Integer(8721));
			_htmlEntities.setObjectForKey("minus", new Integer(8722));
			_htmlEntities.setObjectForKey("lowast", new Integer(8727));
			_htmlEntities.setObjectForKey("radic", new Integer(8730));
			_htmlEntities.setObjectForKey("prop", new Integer(8733));
			_htmlEntities.setObjectForKey("infin", new Integer(8734));
			_htmlEntities.setObjectForKey("ang", new Integer(8736));
			_htmlEntities.setObjectForKey("and", new Integer(8743));
			_htmlEntities.setObjectForKey("or", new Integer(8744));
			_htmlEntities.setObjectForKey("cap", new Integer(8745));
			_htmlEntities.setObjectForKey("cup", new Integer(8746));
			_htmlEntities.setObjectForKey("int", new Integer(8747));
			_htmlEntities.setObjectForKey("there4", new Integer(8756));
			_htmlEntities.setObjectForKey("sim", new Integer(8764));
			_htmlEntities.setObjectForKey("cong", new Integer(8773));
			_htmlEntities.setObjectForKey("asymp", new Integer(8776));
			_htmlEntities.setObjectForKey("ne", new Integer(8800));
			_htmlEntities.setObjectForKey("equiv", new Integer(8801));
			_htmlEntities.setObjectForKey("le", new Integer(8804));
			_htmlEntities.setObjectForKey("ge", new Integer(8805));
			_htmlEntities.setObjectForKey("sub", new Integer(8834));
			_htmlEntities.setObjectForKey("sup", new Integer(8835));
			_htmlEntities.setObjectForKey("nsub", new Integer(8836));
			_htmlEntities.setObjectForKey("sube", new Integer(8838));
			_htmlEntities.setObjectForKey("supe", new Integer(8839));
			_htmlEntities.setObjectForKey("oplus", new Integer(8853));
			_htmlEntities.setObjectForKey("otimes", new Integer(8855));
			_htmlEntities.setObjectForKey("perp", new Integer(8869));
			_htmlEntities.setObjectForKey("sdot", new Integer(8901));
			_htmlEntities.setObjectForKey("lceil", new Integer(8968));
			_htmlEntities.setObjectForKey("rceil", new Integer(8969));
			_htmlEntities.setObjectForKey("lfloor", new Integer(8970));
			_htmlEntities.setObjectForKey("rfloor", new Integer(8971));
			_htmlEntities.setObjectForKey("lang", new Integer(9001));
			_htmlEntities.setObjectForKey("rang", new Integer(9002));
			_htmlEntities.setObjectForKey("loz", new Integer(9674));
			_htmlEntities.setObjectForKey("spades", new Integer(9824));
			_htmlEntities.setObjectForKey("clubs", new Integer(9827));
			_htmlEntities.setObjectForKey("hearts", new Integer(9829));
			_htmlEntities.setObjectForKey("diams", new Integer(9830));
			_htmlEntities.setObjectForKey("quot", new Integer(34));
			_htmlEntities.setObjectForKey("amp", new Integer(38));
			_htmlEntities.setObjectForKey("lt", new Integer(60));
			_htmlEntities.setObjectForKey("gt", new Integer(62));
			_htmlEntities.setObjectForKey("OElig", new Integer(338));
			_htmlEntities.setObjectForKey("oelig", new Integer(339));
			_htmlEntities.setObjectForKey("Scaron", new Integer(352));
			_htmlEntities.setObjectForKey("scaron", new Integer(353));
			_htmlEntities.setObjectForKey("Yuml", new Integer(376));
			_htmlEntities.setObjectForKey("circ", new Integer(710));
			_htmlEntities.setObjectForKey("tilde", new Integer(732));
			_htmlEntities.setObjectForKey("ensp", new Integer(8194));
			_htmlEntities.setObjectForKey("emsp", new Integer(8195));
			_htmlEntities.setObjectForKey("thinsp", new Integer(8201));
			_htmlEntities.setObjectForKey("zwnj", new Integer(8204));
			_htmlEntities.setObjectForKey("zwj", new Integer(8205));
			_htmlEntities.setObjectForKey("lrm", new Integer(8206));
			_htmlEntities.setObjectForKey("rlm", new Integer(8207));
			_htmlEntities.setObjectForKey("ndash", new Integer(8211));
			_htmlEntities.setObjectForKey("mdash", new Integer(8212));
			_htmlEntities.setObjectForKey("lsquo", new Integer(8216));
			_htmlEntities.setObjectForKey("rsquo", new Integer(8217));
			_htmlEntities.setObjectForKey("sbquo", new Integer(8218));
			_htmlEntities.setObjectForKey("ldquo", new Integer(8220));
			_htmlEntities.setObjectForKey("rdquo", new Integer(8221));
			_htmlEntities.setObjectForKey("bdquo", new Integer(8222));
			_htmlEntities.setObjectForKey("dagger", new Integer(8224));
			_htmlEntities.setObjectForKey("Dagger", new Integer(8225));
			_htmlEntities.setObjectForKey("permil", new Integer(8240));
			_htmlEntities.setObjectForKey("lsaquo", new Integer(8249));
			_htmlEntities.setObjectForKey("rsaquo", new Integer(8250));
			_htmlEntities.setObjectForKey("euro", new Integer(8364));
		}
		return _htmlEntities;
	}

	// public static String getDTmotif(EOEnterpriseObject recDT) {
	// StringBuffer sb = new StringBuffer();
	// String motif = normalize((String)recDT.valueForKey("intMotif"));
	// if (motif.length()>0) {
	// sb.append("\n--- Demande de travaux (#").append(recDT.valueForKey("intCleService"));
	// sb.append(", ").append(DateCtrl.dateToString((NSGregorianDate)recDT.valueForKey("intDateCreation")));
	// sb.append(") ---\n\n").append(motif);
	// if (!motif.endsWith("\n")) sb.append("\n");
	// }
	// return sb.toString();
	// }
	//
	// public static String appendDTtraitement(String text, EOEnterpriseObject
	// recTra) {
	// StringBuffer sb = new StringBuffer();
	// String traText = normalize((String)recTra.valueForKey("traTraitement"));
	// sb = sb.append(normalize(text));
	// if (sb.length() > 0) sb.append("\n");
	// if (traText.length() > 0) {
	// sb.append("\n--- Traitement (").append(DateCtrl.dateToString((NSGregorianDate)recTra.valueForKey("traDateDeb")));
	// sb.append(") ---\n\n").append(traText);
	// if (!traText.endsWith("\n")) sb.append("\n");
	// }
	// return sb.toString();
	// }

	/**
	 * Convertie la chaine de caracteres <code>value</code> representant un nombre
	 * en un objet <code>Integer</code>. Le parametre <code>defaultValue</code>
	 * indique la valeur a retourner dans le cas ou la conversion echoue.
	 */
	public static Integer toInteger(String value, Integer defaultValue) {
		if (value == null)
			return defaultValue;
		try {
			value = value.trim();
			return Integer.valueOf(value);
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	public static String formatDouble(double number, int numComma) {
		StringBuffer pattern = new StringBuffer("#0");
		if (numComma > 0) {
			pattern.append(".");
			for (int i = 1; i <= numComma; i++)
				pattern.append("0");
		}
		DecimalFormat formatter = new DecimalFormat(pattern.toString());
		return formatter.format(number);
	}

	public static String formatLocalInfoForHTML(String info) {
		if (StringCtrl.normalize(info).length() > 0) {
			info = StringCtrl.quoteText(info, "&nbsp;&nbsp;&nbsp;&nbsp;");
			info = StringCtrl.replace(info, "\n", "<br>");
		}
		return info;
	}

	public static void main(String[] args) {
		// CktlLog.setLevel(CktlLog.LEVEL_DEBUG);
		// double d = 3445;
		// CktlLog.trace("Original : "+d);
		// CktlLog.trace("Converted : "+formatDouble(d, 2));
	}

	/**
	 * Convertir une chaine de caractere en chaine HTML, en "escapant" tous les
	 * caracteres normalement interpret�s et en replacant les caracteres accentues
	 * et autres par leur code HTML
	 */
	public static String getHTMLString(String str) {
		// String cleanStr = WOMessage.stringByEscapingHTMLAttributeValue(str);

		StringBuffer cleanBuf = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			int c = str.charAt(i);
			String code = (String) htmlEntities().objectForKey(new Integer(c));
			if (code != null)
				cleanBuf.append("&").append(code).append(";");
			else
				cleanBuf.append(Character.toString((char) c));
		}

		String cleanStr = cleanBuf.toString();

		// les caracteres de retour chariot
		cleanStr = StringCtrl.replace(cleanStr, "\r\n", "<BR>");
		cleanStr = StringCtrl.replace(cleanStr, "\n", "<BR>"); // saut de ligne
		cleanStr = StringCtrl.replace(cleanStr, "\r;", "<BR>"); // retour chariot
		cleanStr = StringCtrl.replace(cleanStr, "\f", "<BR>"); // saut de page

		// tabulation
		cleanStr = StringCtrl.replace(cleanStr, "\t", "&nbsp;");
		return cleanStr;
	}

	/**
	 * Methode qui transforme une chaine en une chaine de taille fixe, en
	 * effectuant la completion si la chaine resultat est trop courte.
	 * 
	 * @param string
	 *          : la chaine a modifier
	 * @param size
	 *          : la taille
	 * @param suffix
	 *          : a rajouter a la fin si depassement (facultatif)
	 */
	public static String fitToSize(String string, int size, String suffix) {
		if (string == null)
			string = StringCtrl.emptyString();
		String compact = string;
		if (string.length() > size) {
			compact = string.substring(0, size - 1);
			if (!StringCtrl.isEmpty(suffix))
				compact = compact.substring(0, size - suffix.length()) + suffix;
		}
		// si trop ptit on allonge
		while (compact.length() < size)
			compact += " ";
		return compact;
	}

	/**
	 * Ratailler la chaine pour faire au max maxLen car, quitte a couper les mots
	 * qui depassent.
	 * 
	 * @param s
	 * @param maxLen
	 * @param suffix
	 * @return
	 */
	public static String compactString(String s, int maxLen, String suffix) {
		String compactedString = StringCtrl.compactString(s, maxLen, suffix);
		if (compactedString.length() > maxLen) {
			compactedString = compactedString.substring(0, maxLen) + suffix;
		}
		return compactedString;
	}

	/**
	 * Le cadre generique d'une tooltip. Permet de mettre un titre et un contenu
	 */
	public static String getHtmlToolTipBox(String title, String content) {
		StringBuffer htmlPrefix = new StringBuffer();
		htmlPrefix.append("<table cellspacing=0 border=0 cellpadding=2 width=100%>");
		htmlPrefix.append("<tr>");
		htmlPrefix.append("<td nowrap class=boxTitle><font class=subTitleInverse>");
		htmlPrefix.append("&nbsp;").append(title);
		htmlPrefix.append("</font> </td>");
		htmlPrefix.append("</tr>");
		htmlPrefix.append("<tr>");
		htmlPrefix.append("<td class=boxContents>");

		StringBuffer htmlSuffix = new StringBuffer();
		htmlSuffix.append("</td></tr>");
		htmlSuffix.append("</table>");
		htmlSuffix.append("</td></tr></table>");

		// nettoyage pour compatibilite HTML (le caractere "'")
		String cleanContent = DTStringCtrl.getHTMLString(content);
		content = WOMessage.stringByEscapingHTMLAttributeValue(content);
		cleanContent = StringCtrl.replace(cleanContent, "'", "&#145;");

		StringBuffer htmlText = new StringBuffer();
		htmlText.append(htmlPrefix.toString());
		htmlText.append(cleanContent);
		htmlText.append(htmlSuffix.toString());

		return htmlText.toString();
	}
}

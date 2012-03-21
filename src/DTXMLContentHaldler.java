/*
 * Copyright CRI - Universite de La Rochelle, 1995-2004 
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Effectue l'analyse d'un contenu XML et le presente sous forme d'un
 * dictionnaire.
 * 
 * <p>Cette classe peut etre utilisee comme le gestionnaire d'un contenu
 * XML dans l'analyseur de type SAX. Elle effetue l'analyse XML et memorise
 * sa structure sous forme d'un dictionnaire compose des couples (cle, valeur).
 * La cle correspond au nom d'une balise et value est son contenu. Le resultat
 * de l'analyse peut etre recupere via la methode <code>getValues</code>.</p>
 * 
 * <p>Cet analyseur ne permet d'avoir qu'une seule definition pour une cle.
 * Si plusieurs balises avec le meme nom sont presentes, alors leurs valeurs
 * seront "cumulees".</p>
 *  
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTXMLContentHaldler extends DefaultHandler {
  /** Indique si la balise "root" doit etre ignoree lors de l'analyse */
  private boolean ignoreRoot;
  
  /** Indique le nom de la balise "root" recontre */
  private String rootElement;
  
  /** La liste de toutes les cles */
  private Vector tags = new Vector();
  
  /** Le dictionnaire de toutes les valeurs */
  private Hashtable values = new Hashtable();
  
  /**
   * Cree un nouvelle instance de l'analyseur XML. Le parametre
   * <code>ignoreRoot</code> indique si la balise "racine" du contenu XML doit
   * etre ignoree.
   */
  public DTXMLContentHaldler(boolean ignoreRoot) {
    this.ignoreRoot = ignoreRoot;
    this.tags = new Vector();
    this.values = new Hashtable();
  }

  /**
   * Recoit la notification sur le debut de la balise <code>localName</code>.
   */
  public void startElement(String uri, String localName,
                           String qName, Attributes atts)
  {
    // Tester s'il s'agit de la balise "racine"
    if (rootElement == null) {
      rootElement = localName;
      // Si la racine doit etre ignore, alors... on l'ignore
      if (ignoreRoot) return;
    }
    // ...sinon, on la memorise
    pushTag(localName);
  }
  
  /**
   * Recoit la notification sur la fin de la balise <code>localName</code>.
   */
  public void endElement(String uri, String localName, String qName) {
    popTag(localName);
  }
  
  /**
   * Recoit la notification sur le nouveau contenu de la balise en cours
   * d'analyse. Cette methode peut �tre appellee plusieurs fois pour la
   * m�me balise. La valeur finale doit donc etre "cumulee".
   */
  public void characters(char[] ch, int start, int len) {
    pushValue(new String(ch, start, len));
  }
  
  /**
   * Retourne le dictionnaire de toutes les definitions (<em>cle</em>,&nbsp;<em>valeur</em>) retrouvees dans le contenu XML. La <em>cle</em>
   * correspond a une balise XML. La parametre stripEmty indique si les balises
   * avec le valeurs vides (sans contenu) doivent etre ignorees.
   */
  public Hashtable getValues(boolean stripEmpty) {
    Hashtable resultValues = new Hashtable();
    Enumeration keys = values.keys();
    Object key;
    String value;
    while(keys.hasMoreElements()) {
      key = keys.nextElement();
      value = values.get(key).toString();
      if (stripEmpty) value = value.trim();
      resultValues.put(key, value);
    }
    return resultValues;
  }

  /**
   * Memorise la valeur <code>value</code>. La valeur sera ajoutee a celles
   * deja memorisees pour la balise en cours. Elle est ignoree si aucune balise
   * en cours n'existe.
   */
  private void pushValue(String value) {
    if (currentTag() != null) {
      StringBuffer prevValue = (StringBuffer)values.get(currentTag());
      if (prevValue == null)
        prevValue = new StringBuffer();
      prevValue.append(value);
      values.put(currentTag(), prevValue);
    }
  }
  
  /**
   * Retourne le nom de la balise en cours d'ananlyse. Retourne <em>null</em> si
   * aucune balise n'est encore analyse.
   */
  private String currentTag() {
    if (tags.size() > 0)
      return (String)tags.elementAt(tags.size()-1);
    else
      return null;
  }
  
  /**
   * Memorise la balise <code>tag</code> dans la liste des balise analysees.
   * Elle devient la balise en cours d'analyse.
   */
  private void pushTag(String tag) {
    tags.addElement(tag);
  }
  
  /**
   * Retire la balise de la liste des balise en cours d'analyse. La balyse
   * "precedente" devient la balise en cours.
   */
  private void popTag(String tag) {
    if ((tags.size() > 0) && currentTag().equals(tag))
      tags.removeElementAt(tags.size()-1);
  }
}

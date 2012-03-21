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
import org.cocktail.dt.server.metier.EOVJefyDestin;

/**
 * Une entre pour la liste des destinations. Elle est toujours creee sur un
 * objet de la table <i>JefyDestin</i>.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDestinListItem implements Cloneable {
	// Une entree dans la table "VJefyDestin"
	private String dstLibelle;
	private Number lolfId;

	/**
	 * Cree une entre qui va prendre les valeurs d'un enregistrement de la vue
	 * <i>VJefyDestin</i> (<em>dstLib</em> et <em>lolfId</em>).
	 */
	public DTDestinListItem(EOVJefyDestin recDestin) {
		dstLibelle = recDestin.dstLib();
		lolfId = recDestin.lolfId();
	}

	/**
	 * Cree une entre qui va reprensenter une destination avec le libelle et le
	 * code donnes.
	 */
	public DTDestinListItem(String libelle, Number id) {
		dstLibelle = libelle;
		lolfId = id;
	}

	/**
	 * Retourne le libelle de la destination.
	 */
	public String dstLibelle() {
		return dstLibelle;
	}

	/**
	 * Retourne le code de la destination.
	 */
	public Number lolfId() {
		return lolfId;
	}

	/**
	 * Retourne le libelle de la destination.
	 */
	public String toString() {
		return dstLibelle();
	}
}

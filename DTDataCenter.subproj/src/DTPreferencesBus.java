/*
 * Copyright CRI - Universite de La Rochelle, 2001-2005 
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

import org.cocktail.dt.server.metier.EOPrefAppli;
import org.cocktail.dt.server.metier.EOPrefDefaut;
import org.cocktail.dt.server.metier.EOPrefDroits;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.eof.ERXQ;

/**
 * Permet de gerer les preferences de l'utilisateur. Cette classe permet
 * d'acceder a l'ensemble de differents preferences : parametrage de
 * l'application, selections par defaut, definitions des couleurs, etc...
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTPreferencesBus extends DTDataBus {
	/** L'ordre de tri par nom d'une personne */
	private NSArray sortNomUsuel;
	/** Les codes des couleurs definies par l'utilisateur */
	private NSMutableDictionary colorCodes;
	/** La liste des objets NSColor correspondants aux codes des couleurs */
	private NSMutableDictionary colorObjects;
	/** Definition explicite, s'il faut afficher les photos des utilisateurs */
	private Boolean usePhoto;

	/**
	 * Cree une instance du gestionnaire des preferences.
	 */
	public DTPreferencesBus(EOEditingContext eocontext) {
		super(eocontext);
		colorCodes = new NSMutableDictionary();
		colorObjects = new NSMutableDictionary();
		sortNomUsuel = CktlSort.newSort("nomUsuel");
	}

	/**
	 * Reinitialise les definitions des couleurs pour les interventions de
	 * l'intervenant ayant l'identifiant <code>noIndividu</code>. Les preferences
	 * sont prises a partir de l'entite <i>PrefCouleurs</i> et de l'entite
	 * <i>PrefCouleursAct</i>.
	 */
	public void reinitColors(Number noIndividu) {
		NSArray objects = fetchArray("PrefCouleurs", "noIndividu=" + noIndividu, null, null);
		objects = objects.arrayByAddingObjectsFromArray(
				fetchArray("PrefCouleursAct", "noIndividu=" + noIndividu, null, null));
		CktlRecord rec;
		colorCodes.removeAllObjects();
		colorObjects.removeAllObjects();
		for (int i = 0; i < objects.count(); i++) {
			rec = (CktlRecord) objects.objectAtIndex(i);
			boolean isBatiment = true;
			try {
				rec.valueForKey("cLocal");
			} catch (Exception e) {
				isBatiment = false;
			}
			String pkString = null;
			if (isBatiment)
				pkString = rec.stringForKey("cLocal");
			else
				pkString = Integer.toString(rec.intForKey("actOrdre"));
			colorCodes.setObjectForKey(rec.valueForKey("couleur"), pkString);
		}
	}

	/**
	 * Reinitialise toutes les preferences de l'intervenant actuellement connecte
	 * a l'application.
	 */
	public NSDictionary getAllCurrentPreferences() {
		return getAllPreferences(userInfo().noIndividu(),
															userInfo().dtServiceCode());
	}

	/**
	 * Initialise les preferences de l'intervenant ayant le code
	 * <code>noIndividu</code> et appartenant au service <code>service</code>.
	 */
	private NSDictionary getAllPreferences(Number noIndividu, String service) {
		NSMutableDictionary prefs;
		EOPrefAppli recAppli = findPrefAppli(noIndividu);
		if (recAppli == null)
			return null;
		EOPrefDroits recDroits = findPrefDroits(noIndividu, service);
		prefs = new NSMutableDictionary();
		prefs.takeValueForKey(recAppli.prfDroService(), "prfDroService");
		prefs.takeValueForKey(recAppli.prfEtatCode(), "prfEtatCode");
		prefs.takeValueForKey(recAppli.prfOnglet(), "prfOnglet");

		prefs.takeValueForKey(recAppli.prfOrdreColumns(), "prfOrdreColumns");
		prefs.takeValueForKey(recAppli.prfPhoto(), "prfPhoto");
		prefs.takeValueForKey(recAppli.prfTimer(), "prfTimer");
		prefs.takeValueForKey(recAppli.prfTri(), "prfTri");
		prefs.takeValueForKey(recAppli.prfMailTraitement(), "prfMailTraitement");
		prefs.takeValueForKey(recAppli.prfTriInt(), "prfTriInt");
		prefs.takeValueForKey(recAppli.prfTriTra(), "prfTriTra");
		prefs.takeValueForKey(recAppli.prfAideSysNav(), "prfAideSysNav");

		if (recDroits != null) {
			prefs.takeValueForKey(recDroits.droNiveau(), "droNiveau");
			prefs.takeValueForKey(recDroits.prfNoIndIntervenant(), "prfNoIndIntervenant");
		}

		prefs.takeValueForKey(recAppli.prfNbIntPerPage(), "prfNbIntPerPage");
		prefs.takeValueForKey(recAppli.prfUseMailInterne(), "prfUseMailInterne");
		prefs.takeValueForKey(recAppli.prfUseCoulBat(), "prfUseCoulBat");
		prefs.takeValueForKey(recAppli.prfInsertDtSig(), "prfInsertDtSig");
		prefs.takeValueForKey(recAppli.prfExportPlanning(), "prfExportPlanning");
		prefs.takeValueForKey(recAppli.prfControleChevauchementPlanning(), EOPrefAppli.PRF_CONTROLE_CHEVAUCHEMENT_PLANNING_KEY);
		prefs.takeValueForKey(recAppli.prfConfirmationCloture(), EOPrefAppli.PRF_CONFIRMATION_CLOTURE_KEY);
		prefs.takeValueForKey(recAppli.prfSauvegarderPanier(), EOPrefAppli.PRF_SAUVEGARDER_PANIER_KEY);
		prefs.takeValueForKey(recAppli.prfPanier(), EOPrefAppli.PRF_PANIER_KEY);

		reinitColors(noIndividu);

		return prefs;
	}

	/**
	 * Retourne les preference application de la personne avec le code
	 * <code>noIndividu</code>. Les preferences sont lues dans l'entite
	 * <i>PrefAppli</i>.
	 */
	public EOPrefAppli findPrefAppli(Number noIndividu) {
		return (EOPrefAppli) fetchObject(EOPrefAppli.ENTITY_NAME, EOPrefAppli.NO_INDIVIDU_KEY + "=" + noIndividu, null);
	}

	/**
	 * Retourne la definition des droits pour la personne avec le code
	 * <code>noInvidu</code> et appartenant au service <code>codeService</code>.
	 * Les informations sont lues dans l'entite <i>PrefDroits</i>.
	 */
	public EOPrefDroits findPrefDroits(Number noIndividu, String codeService) {
		return (EOPrefDroits) fetchObject(
				EOPrefDroits.ENTITY_NAME, EOPrefDroits.NO_INDIVIDU_KEY + "=" + noIndividu + " and " + EOPrefDroits.DRO_SERVICE_KEY + "='" + codeService + "'", null);
	}

	/**
	 * Retourne toutes les definitions des droits de la personne avec
	 * l'identifiant <code>noIndividu</code>. Plusieurs definitions sont
	 * retournees si la personne est enregistree comme l'intervenant de plusieurs
	 * services.
	 */
	public NSArray<EOPrefDroits> findAllPrefDroits(Number noIndividu) {
		return fetchArray(EOPrefDroits.ENTITY_NAME, EOPrefDroits.NO_INDIVIDU_KEY + "=" + noIndividu, null, null);
	}

	/**
	 * Retourne le code de niveau des droits de l'intervenant
	 * <code>noIndividu</code> appartenant au service <code>codeService</code>.
	 * Retourne -1 si la personne n'appartient pas au service indique.
	 */
	public int getDroitsForService(Number noIndividu, String codeService) {
		CktlRecord rec = findPrefDroits(noIndividu, codeService);
		if (rec != null)
			return rec.intForKey("droNiveau");
		return -1;
	}

	/**
	 * Retourne les definitions des droits de tous les intervenants appartenants
	 * au service <code>codeService</code>. La liste est constitue des
	 * enregistrements de l'entite <i>PrefDroits</i>.
	 */
	public NSArray findAllIntervenantsService(String codeService) {
		return fetchArray("PrefDroits", newCondition("droService='" + codeService + "'"), sortNomUsuel);
	}

	/**
	 * Retourne le code du service par defaut de l'intervenant
	 * <code>noIndividu</code>. C'est le service qui a ete choisi en dernier.
	 * Retourne <i>null</i> si le service ne peux pas etre determine.
	 */
	public String getDefaultServiceCode(Number noIndividu) {
		CktlRecord rec = findPrefAppli(noIndividu);
		if (rec == null)
			return null;
		String dCode = rec.stringForKey("prfDroService");
		// Il faut verifier si le service existe bien. Parfois, un intervenant peut
		// etre supprime d'un service, mais garder la reference dans ses
		// preferences.
		NSArray allServices = findAllPrefDroits(noIndividu);
		if (allServices.count() > 0) {
			for (int i = 0; i < allServices.count(); i++) {
				rec = (CktlRecord) allServices.objectAtIndex(i);
				if (rec.valueForKey("droService").equals(dCode))
					return dCode;
			}
			// Si l'affectation n'existe pas, on va prendre le premier service dispo
			dCode = ((CktlRecord) allServices.objectAtIndex(0)).stringForKey("droService");
			// ... et on le memorise comme le service par defaut
			updatePrefAppli(null, noIndividu, dCode,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			return dCode;
		}
		return null;
	}

	/**
	 * Retourne les codes de tous les services auquels l'utilisateur appartient
	 * avec le niveau des droits indique. Retourne tous les services si
	 * <code>droNiveau</code> est -1.
	 */
	public NSArray<String> getAllServiceCodes(Number noIndividu, int droNiveau) {
		NSArray<EOPrefDroits> allDroits = findAllPrefDroits(noIndividu);
		NSMutableArray<String> codes = new NSMutableArray<String>();
		for (int i = 0; i < allDroits.count(); i++) {
			EOPrefDroits rec = allDroits.objectAtIndex(i);
			if (droNiveau == -1 || rec.droNiveau().intValue() >= droNiveau) {
				codes.addObject(rec.droService());
			}
		}
		return codes;
	}

	/**
	 * Remplit le "display group" <code>batimentsTable</code> des informations de
	 * l'entite <i>Batiment</i>.
	 */
	public void fetchBatiments(WODisplayGroup batimentsTable) {
		fetchTable(batimentsTable, null, null, true);
	}

	/**
	 * Remplit le "display group" <code>table</code> de la liste de tous les
	 * intervenant du service <code>codeService</code>.
	 */
	public void fetchIntervenantsService(WODisplayGroup table, String codeService) {
		clearTable(table);
		fetchTable(table, newCondition("droService='" + codeService + "'"), sortNomUsuel, true);
	}

	/**
	 * Retourne les informations sur l'utilisateur avec le login donne.
	 */
	public DTUserInfo getUserInfoForLogin(String login) {
		DTUserInfo infos = new DTUserInfo();
		infos.compteForLogin(login, null, true);
		// On verifie, s'il y a une erreur kelkonku
		if (infos.errorCode() != CktlUserInfo.ERROR_NONE)
			return infos;
		// Est-ce un utilisateur, un intervenant ou un affectant ? Et pour quel
		// service ?
		resetDroitsForAllService(infos);
		// Les preferences de l'individu, s'il est intervenant
		infos.setPreferences(getAllPreferences(infos.noIndividu(), infos.dtServiceCode()));
		CktlLog.trace("userInfo.service : " + infos.codeService());
		return infos;
	}

	/**
	 * Ajoute a la definition des informations sur l'utilisateur
	 * <code>infos</code> les donnees specifiques au service
	 * <code>serviceCode</code>. Le paremetre infos doit deja contenir les
	 * informations sur l'utilisateur (son identifiant "noInvidu"). Si le code du
	 * service <code>serviceCode</code> est <i>null</i>, alors les informations
	 * sont initiailisees a celles du service par defaut.
	 */
	private void resetDroitsForService(String serviceCode, DTUserInfo infos) {
		// Est-ce un utilisateur, un intervenant ou un affectant ? Et pour quel
		// service ?
		CktlRecord rec = null;
		if (serviceCode == null)
			serviceCode = getDefaultServiceCode(infos.noIndividu());
		if (serviceCode != null)
			rec = findPrefDroits(infos.noIndividu(), serviceCode);
		// Si l'utilisateur n'est pas un intervenant
		// alors il aura les droits de base : 0
		if (rec == null) {
			infos.setDTServiceCode("-1");
			infos.setDroits(new Integer(0), infos.dtServiceCode());
			infos.setDTServiceLibelle(StringCtrl.emptyString());
		} else {
			infos.setDTServiceCode(rec.stringForKey("droService"));
			infos.setDroits(rec.numberForKey("droNiveau"), infos.dtServiceCode());
			infos.setDTServiceLibelle(rec.stringForKeyPath("toStructureUlr.llStructure"));
		}
		CktlLog.trace("noInvididu : " + infos.noIndividu() + ", serviceCode : " + infos.dtServiceCode());
	}

	/**
	 * Ajoute a la definition des informations sur l'utilisateur
	 * <code>infos</code> les droits sur tous les service que peut avoir
	 * l'individu
	 */
	public void resetDroitsForAllService(DTUserInfo infos) {
		// tous les droits declares pour cet utilisiteur
		NSArray recsDroit = findAllPrefDroits(infos.noIndividu());
		for (int i = 0; i < recsDroit.count(); i++) {
			CktlRecord recDroit = (CktlRecord) recsDroit.objectAtIndex(i);
			infos.setDTServiceCode(recDroit.stringForKey("droService"));
			infos.setDroits(recDroit.numberForKey("droNiveau"), recDroit.stringForKey("droService"));
			infos.setDTServiceLibelle(recDroit.stringForKeyPath("toStructureUlr.llStructure"));
		}
		// enfin le service par default
		resetDroitsForService(null, infos);
	}

	/**
	 * Mets a jour les preferences application de l'utilisateur
	 * <code>noInvidu</code>.
	 * 
	 * @param exportPlanning
	 *          TODO
	 * @param controleChevauchementPlanning
	 *          TODO
	 * @param confirmationCloture
	 *          TODO
	 * @param sauvegarderPanier
	 *          TODO
	 * @param panier
	 *          La liste des (intOrdre) séparés par des ";" du panier
	 */
	public boolean updatePrefAppli(Integer transId, Number noIndividu,
																	String codeService, String etat,
																	Boolean mailTraitementState, Number onglet,
																	Boolean photoState, Number timer,
																	String triInt, String triTra,
																	Boolean aideSysNav, Number nbIntPerPage,
																	Boolean useMailInterne, Boolean useCoulBat,
																	Boolean insertDtSig, Boolean exportPlanning,
																	Boolean controleChevauchementPlanning,
																	Boolean confirmationCloture,
																	Boolean sauvegarderPanier,
																	String panier) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = fetchObject(
					ec, EOPrefAppli.ENTITY_NAME, newCondition("noIndividu=" + noIndividu));
			rec.takeStoredValueForKey(noIndividu, "noIndividu");
			if (codeService != null)
				rec.takeStoredValueForKey(codeService, "prfDroService");
			if (etat != null)
				rec.takeStoredValueForKey(etat, "prfEtatCode");
			if (mailTraitementState != null)
				rec.takeStoredValueForKey(
						(mailTraitementState.booleanValue() ? new Integer(1) : new Integer(0)),
						"prfMailTraitement");
			if (onglet != null)
				rec.takeStoredValueForKey(onglet, "prfOnglet");
			if (photoState != null) {
				rec.takeStoredValueForKey(
						(photoState.booleanValue() ? new Integer(1) : new Integer(0)),
						"prfPhoto");
				setUsePhoto(photoState.booleanValue());
			}
			if (timer != null)
				rec.takeStoredValueForKey(timer, "prfTimer");
			if (triInt != null)
				rec.takeStoredValueForKey(triInt, "prfTriInt");
			if (triTra != null)
				rec.takeStoredValueForKey(triTra, "prfTriTra");
			if (aideSysNav != null)
				rec.takeStoredValueForKey(aideSysNav.booleanValue() ? "O" : "N", "prfAideSysNav");
			if (nbIntPerPage != null)
				rec.takeStoredValueForKey(nbIntPerPage, "prfNbIntPerPage");
			if (useMailInterne != null)
				rec.takeStoredValueForKey(useMailInterne.booleanValue() ? new Integer(1) : new Integer(0), "prfUseMailInterne");
			if (useCoulBat != null)
				rec.takeStoredValueForKey(useCoulBat.booleanValue() ? new Integer(1) : new Integer(0), "prfUseCoulBat");
			if (insertDtSig != null)
				rec.takeStoredValueForKey(insertDtSig.booleanValue() ? new Integer(1) : new Integer(0), "prfInsertDtSig");
			if (exportPlanning != null)
				rec.takeStoredValueForKey(exportPlanning.booleanValue() ? new Integer(1) : new Integer(0), "prfExportPlanning");

			if (controleChevauchementPlanning != null) {
				Integer value = new Integer(0);
				if (controleChevauchementPlanning.booleanValue() == true) {
					value = new Integer(1);
				}
				rec.takeStoredValueForKey(value, EOPrefAppli.PRF_CONTROLE_CHEVAUCHEMENT_PLANNING_KEY);
			}

			if (confirmationCloture != null) {
				Integer value = new Integer(0);
				if (confirmationCloture.booleanValue() == true) {
					value = new Integer(1);
				}
				rec.takeStoredValueForKey(value, EOPrefAppli.PRF_CONFIRMATION_CLOTURE_KEY);
			}

			if (sauvegarderPanier != null) {
				Integer value = new Integer(0);
				if (sauvegarderPanier.booleanValue() == true) {
					value = new Integer(1);
				}
				rec.takeStoredValueForKey(value, EOPrefAppli.PRF_SAUVEGARDER_PANIER_KEY);
			}

			if (panier != null) {
				rec.takeStoredValueForKey(panier, EOPrefAppli.PRF_PANIER_KEY);
			}

			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Mets a jour les definitions des droits pour l'intervenant
	 * <code>noInvidu</code> appartenant au service <code>codeService</code>.
	 */
	public boolean updatePrefDroits(
			Integer transId, Number noIndividu, String codeService,
			String intervenants, Integer droNiveau) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = fetchObject(
					ec, "PrefDroits", newCondition("noIndividu=" + noIndividu + " and droService='" + codeService + "'"));
			// On ajoute la definition si elle n'est pas encore ajoutee
			if (rec == null) {
				if (!addPrefDroits(localTransId, noIndividu, codeService, droNiveau))
					return false;
			} else {
				// Sinon, on la met a jour
				if (intervenants != null)
					rec.takeStoredValueForKey(intervenants, "prfNoIndIntervenant");
				if (droNiveau != null)
					rec.takeStoredValueForKey(droNiveau, "droNiveau");
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Retourne le code de la couleur pour le batiment ou l'activite avec le code
	 * <code>code</code>.
	 */
	public String getColorForCode(String codeString) {
		return (String) colorCodes.valueForKey(codeString);
	}

	/**
	 * Retourne le code couleur du type d'un NSColor vers une couleur HTML pour un
	 * batiment ou par une activite specifie par <code>code</code> ex NSColor :
	 * 0.294333(R):1.000000(V):0.013927(B):1.000000(Alpha)
	 */
	public String getHtmlColorForCode(Object code) {
		String htmlColor = null;
		String nsColor = (code != null ? getColorForCode(
				(code instanceof String ? (String) code : Integer.toString(((Number) code).intValue()))) : null);
		if (nsColor != null) {
			NSArray couches = NSArray.componentsSeparatedByString(nsColor, ":");
			htmlColor = "";
			// recuperer l'alpha
			double coefAlpha = Double.parseDouble((String) couches.lastObject());
			for (int i = 0; i < 3; i++) {
				double value = Double.parseDouble((String) couches.objectAtIndex(i)) * 255.0 * coefAlpha;
				htmlColor += Integer.toHexString((int) value);
			}
		}
		return (htmlColor != null ? "#" + htmlColor.toUpperCase() : null);
	}

	/**
	 * Transforme une couleur en HTML vers une couleur en NSColor definit dans
	 * <code>htmlColor</code>
	 */
	public String htmlColorToNSColor(String htmlColor) {
		String nsColor = null;
		if (htmlColor != null) {
			int red = Integer.parseInt(htmlColor.substring(1, 3), 16);
			int gre = Integer.parseInt(htmlColor.substring(3, 5), 16);
			int ble = Integer.parseInt(htmlColor.substring(5, 7), 16);
			// mettre en double
			double redD = ((double) red) / 256;
			double greD = ((double) gre) / 256;
			double bleD = ((double) ble) / 256;
			DecimalFormat df = new DecimalFormat("#.######");
			nsColor = df.format(redD) + ":" + df.format(greD) + ":" + df.format(bleD) + ":1.000000";
			// virer les virgules
			nsColor = StringCtrl.replace(nsColor, ",", ".");
		}
		return nsColor;
	}

	/**
	 * Memorise le code de la couleur <code>color</code> pour le batiment ayant le
	 * code <code>code</code> ou l'activite ayant comme code <code>code</code
	 */
	public void setColorForCode(String color, Object code) {
		colorCodes.setObjectForKey(color,
				(code instanceof String ? (String) code : Integer.toString(((Number) code).intValue())));
	}

	// /**
	// * Retourne un objet <code>NSColor</code> qui correspond a la couleur
	// * associee au batiment avec le code <code>codeLocal</code>.
	// */
	// public NSColor getColorObjectForLocal(String codeLocal) {
	// // Tester s'il n'existe pas encore
	// NSColor color = (NSColor)colorObjects.valueForKey(codeLocal);
	// if (color == null) {
	// // Sinon, on genere et on memorise un nouvel objet
	// String colorCode = (String)colorCodes.objectForKey(codeLocal);
	// if (colorCode != null) {
	// color = getColorForCode(colorCode);
	// // on le memorise
	// if (color != null)
	// colorObjects.setObjectForKey(color, codeLocal);
	// }
	// }
	// return color;
	// }

	// /**
	// * Retourne un objet <code>NSColor</code> correspondant au code de la
	// couleur
	// * <code>colorCode</code>. Le code doit etre compose de quatre codes separes
	// * par ":" ("<code>R:G:B:Alpha</code>").
	// */
	// private NSColor getColorForCode(String colorCode) {
	// if (StringCtrl.normalize(colorCode).length() > 0) {
	// NSArray rgba = NSArray.componentsSeparatedByString(colorCode, ":");
	// return NSColor.colorWithCalibratedRGB(
	// (new Float((String)rgba.objectAtIndex(0))).floatValue(),
	// (new Float((String)rgba.objectAtIndex(1))).floatValue(),
	// (new Float((String)rgba.objectAtIndex(2))).floatValue(),
	// (new Float((String)rgba.objectAtIndex(3))).floatValue());
	// }
	// return null;
	// }

	/**
	 * Sauvegarde les definitions d'associations entre les couleurs et les
	 * batiments pour l'individiu <code>noIndividu</code>. Cette methode
	 * sauvegarde dans la base de donnees toutes les definitions qui sont
	 * memorisees dans le cache interne (<code>colorCodes</code>).
	 * 
	 * @see #setColorForCode(String, String)
	 */
	public boolean updatePrefCouleurs(Integer transId, Number noIndividu) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec;
			NSArray primaryKeys = colorCodes.allKeys();
			NSArray prefs;
			NSArray objects = fetchArray(ec, "PrefCouleurs", newCondition("noIndividu=" + noIndividu), null);
			NSArray objectsAct = fetchArray(ec, "PrefCouleursAct", newCondition("noIndividu=" + noIndividu), null);
			// Mettre a jour et ajouter nouvelle definitions
			for (int i = 0; i < primaryKeys.count(); i++) {
				String aPk = (String) primaryKeys.objectAtIndex(i);
				boolean isBatiment = false;
				try {
					Integer.parseInt(aPk);
				} catch (Throwable e) {
					isBatiment = true;
				}
				if (isBatiment)
					prefs = EOQualifier.filteredArrayWithQualifier(
							objects, newCondition("cLocal='" + aPk + "'"));
				else
					prefs = EOQualifier.filteredArrayWithQualifier(
							objectsAct, newCondition("actOrdre=%@", new NSArray(new Integer(Integer.parseInt(aPk)))));
				if (prefs.count() > 0) {
					// Si la definition existe deja, on la met a jour
					rec = (EOEnterpriseObject) prefs.objectAtIndex(0);
					rec.takeValueForKey(colorCodes.objectForKey(aPk), "couleur");
				} else {
					// Sinon, on ajoute un nouveau objet
					rec = null;
					if (isBatiment) {
						rec = EOUtilities.createAndInsertInstance(ec, "PrefCouleurs");
						rec.takeStoredValueForKey(aPk, "cLocal");
					} else {
						rec = EOUtilities.createAndInsertInstance(ec, "PrefCouleursAct");
						rec.takeStoredValueForKey(new Integer(Integer.parseInt(aPk)), "actOrdre");
					}
					rec.takeStoredValueForKey(colorCodes.objectForKey(primaryKeys.objectAtIndex(i)), "couleur");
					rec.takeStoredValueForKey(noIndividu, "noIndividu");
				}
			}
			// Supprimer les definitions qui ne sont plus utilisees
			for (int i = 0; i < objects.count(); i++) {
				rec = (EOEnterpriseObject) objects.objectAtIndex(i);
				if (colorCodes.objectForKey(rec.valueForKey("cLocal")) == null)
					ec.deleteObject(rec);
			}
			for (int i = 0; i < objectsAct.count(); i++) {
				rec = (EOEnterpriseObject) objectsAct.objectAtIndex(i);
				if (colorCodes.objectForKey(String.valueOf(((Number) rec.valueForKey("actOrdre")).intValue())) == null)
					ec.deleteObject(rec);
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * @deprecated utilise uniquement par l'application DTWindows
	 * 
	 *             Mets a jour la definition <code>colDef</code> des colones
	 *             (ordre, largeur) dans la liste des interventions consultees par
	 *             l'utilisateur <code>noIndividu</code> l'onglet
	 *             <i>Consultation</i>.
	 * 
	 *             <p>
	 *             Retourne respectivement <i>true</i> ou <i>false</i> dans le cas
	 *             du succes ou de l'echeque de l'operation.
	 *             </p>
	 */
	public boolean updatePrefColonnes(Integer transId, Number noIndividu, String colDef) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec =
					fetchObject(ec, "PrefAppli", newCondition("noIndividu=" + noIndividu));
			if (rec == null)
				return false;
			rec.takeStoredValueForKey(colDef, "prfOrdreColumns");
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Ajoute une nouvelle definition des preferences application pour
	 * l'utilisateur <code>noIndividu</code> appartenant au service
	 * <code>codeService</code>.
	 * 
	 * <p>
	 * Retourne respectivement <i>true</i> ou <i>false</i> dans le cas du succes
	 * ou de l'echeque de l'operation.
	 * </p>
	 */
	private boolean addNewPrefAppli(Integer transId, Number noIndividu, String codeService) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOPrefAppli rec = (EOPrefAppli) fetchObject(ec,
					EOPrefAppli.ENTITY_NAME,
					ERXQ.equals(EOPrefAppli.NO_INDIVIDU_KEY, noIndividu));
			if (rec != null)
				return true;
			rec = (EOPrefAppli) EOUtilities.createAndInsertInstance(ec, EOPrefAppli.ENTITY_NAME);
			rec.takeStoredValueForKey(noIndividu, "noIndividu");
			rec.takeStoredValueForKey(codeService, "prfDroService");
			rec.takeStoredValueForKey("C", "prfEtatCode");
			rec.takeStoredValueForKey(new Integer(0), "prfMailTraitement");
			rec.takeStoredValueForKey(new Integer(1), "prfOnglet");
			// TODO champ utilise uniquement par DTWindows
			rec.takeStoredValueForKey(" ", "prfOrdreColumns");
			rec.takeStoredValueForKey(new Integer(1), "prfPhoto");
			rec.takeStoredValueForKey(new Integer(60), "prfTimer");
			rec.takeStoredValueForKey(CODE_TRI_NUMERO, "prfTri");
			rec.takeStoredValueForKey(DESCENDING, "prfTriInt");
			rec.takeStoredValueForKey(DESCENDING, "prfTriTra");
			rec.takeStoredValueForKey("O", "prfAideSysNav");
			rec.takeStoredValueForKey(new Integer(10), "prfNbIntPerPage");
			rec.takeStoredValueForKey(new Integer(0), "prfUseMailInterne");
			rec.takeStoredValueForKey(new Integer(1), "prfUseCoulBat");
			rec.takeStoredValueForKey(new Integer(1), "prfInsertDtSig");
			rec.takeStoredValueForKey(new Integer(0), "prfExportPlanning");
			rec.setPrfControleChevauchementPlanning(new Integer(0));
			rec.setPrfConfirmationCloture(new Integer(1));
			rec.setPrfSauvegarderPanier(new Integer(1));
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Ajoute un nouvelle definition des droits d'intervenant
	 * <code>noIndividu</code> appartenant au service <code>codeService</code>. Le
	 * parametre <code>droNiveau</code> indique le niveau des droits (0-4).
	 * 
	 * <p>
	 * Cette operation enregistre l'utilisateur comme intervenant du service avec
	 * les droits indiques.
	 * </p>
	 * 
	 * <p>
	 * Retourne respectivement <i>true</i> ou <i>false</i> dans le cas du succes
	 * ou de l'echeque de l'operation.
	 * </p>
	 */
	private boolean addPrefDroits(Integer transId, Number noIndividu, String codeService, Integer droNiveau) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = EOUtilities.createAndInsertInstance(ec, "PrefDroits");
			rec.takeStoredValueForKey(noIndividu, "noIndividu");
			rec.takeStoredValueForKey(codeService, "droService");
			rec.takeStoredValueForKey(droNiveau, "droNiveau");
			rec.takeStoredValueForKey(noIndividu.toString(), "prfNoIndIntervenant");
			if (!addNewPrefAppli(localTransId, noIndividu, codeService))
				return false;
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprime la definition des droits de l'intervenant <code>noIndividu</code>
	 * appartenant au service <code>codeService</code>.
	 * 
	 * <p>
	 * Cette operation supprime l'utilisateur de la liste des intervenants du
	 * service <code>codeService</code>.
	 * </p>
	 */
	public boolean deletePrefDroits(Integer transId, Number noIndividu, String codeService) {
		Integer localTransId = getTransaction(transId);
		// EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			NSArray tousDroits = fetchArray("PrefDroits", newCondition("noIndividu=" + noIndividu), null);
			EOQualifier condition = newCondition("noIndividu=" + noIndividu + " and droService='" + codeService + "'");
			if (deleteFromTable(localTransId, "PrefDroits", condition) == -1)
				return false;
			if (tousDroits.count() == 1) {
				if (!deletePrefCouleurs(localTransId, noIndividu))
					return false;
				if (!deletePrefAppli(localTransId, noIndividu))
					return false;
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprime les definitions des couleurs de l'utilisateur
	 * <code>noIndividu</code>.
	 */
	private boolean deletePrefCouleurs(Integer transId, Number noIndividu) {
		return (deleteFromTable(transId, "PrefCouleurs", newCondition("noIndividu=" + noIndividu)) != -1) &&
				(deleteFromTable(transId, "PrefCouleursAct", newCondition("noIndividu=" + noIndividu)) != -1);
	}

	/**
	 * Supprime la definition des preferences application de l'utilisateur
	 * <code>noIndividu</code>.
	 */
	private boolean deletePrefAppli(Integer transId, Number noIndividu) {
		return (deleteFromTable(transId, "PrefAppli", newCondition("noIndividu=" + noIndividu)) != -1);
	}

	/**
	 * Mets a jour les definitions des selections "par defaut" pour l'utilisateur
	 * <code>noInvidu</code>.
	 */
	public boolean updatePrefDefaut(
			Integer transId, Number noIndividu, Number ctOrdre,
			Number orgOrdre, String tcdCode, String dstCode,
			String cStructure, Number orgId, Number lolfId) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOPrefDefaut rec = (EOPrefDefaut) fetchObject(
					ec, EOPrefDefaut.ENTITY_NAME, newCondition(EOPrefDefaut.NO_INDIVIDU_KEY + "=" + noIndividu));
			// On ajoute la definition si elle n'est pas encore ajoutee
			if (rec == null) {
				rec = (EOPrefDefaut) EOUtilities.createAndInsertInstance(ec, EOPrefDefaut.ENTITY_NAME);
				rec.setNoIndividu(new Integer(noIndividu.intValue())); // PK
			}
			// Ensuite, on mets a jour les informations
			if (ctOrdre != null) {
				rec.setPrfCtOrdre(new Integer(ctOrdre.intValue()));
			}
			if (orgOrdre != null) {
				rec.setPrfOrgOrdre(new Integer(orgOrdre.intValue()));
			}
			if (tcdCode != null) {
				rec.setPrfTcdCode(tcdCode);
			}
			if (dstCode != null) {
				rec.setPrfDstCode(dstCode);
			}
			if (cStructure != null) {
				rec.setPrfCStructure(cStructure);
			}
			if (orgId != null) {
				rec.setPrfOrgId(new Integer(orgId.intValue()));
			}
			if (lolfId != null) {
				rec.setPrfLolfId(new Integer(lolfId.intValue()));
			}
			if (transId == null) {
				commitECTransaction(localTransId);
			}
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Retourne les definitions des selections "par defaut" pour l'utilisateur
	 * <code>noIndividu</code>.
	 */
	public CktlRecord findPrefDefaut(Number noIndividu) {
		return (CktlRecord) fetchObject("PrefDefaut", newCondition("noIndividu=" + noIndividu));
	}

	/**
	 * Test si l'application doit afficher les photos des utilisateurs, si elles
	 * sont disponibles. Si cette valeur est definit explicitement avec la methode
	 * setUsePhoto, alors celle-ci est utilsee. Sinon, la methode retourne la
	 * valeur de parametre GRHUM_PHOTO de la configuration de l'application.
	 */
	public boolean usePhoto() {
		if (usePhoto == null) {
			// Le support de photos, est il installe ?
			usePhoto = new Boolean(dtApp().config().booleanForKey("GRHUM_PHOTO"));
			// Si le support est disponible, regarde dans le prefs personnels
			if (usePhoto.booleanValue())
				usePhoto = new Boolean(userInfo().usePhoto());
		}
		return usePhoto.booleanValue();
	}

	/**
	 * Definit explicitement si l'applicaition doit afficher les photos des
	 * utilisateurs si celles-ci sont disponibles.
	 */
	public void setUsePhoto(boolean flag) {
		usePhoto = new Boolean(flag);
	}

	// Rajouts pour DTWeb uniquement

	/** Tri des DT par leur numeros */
	public final static String CODE_TRI_NUMERO = "numero";
	/** Tri des DT par leur date de creation */
	public final static String CODE_TRI_DATE = "date";
	/** Tri des DT par leur nom du demandeur */
	public final static String CODE_TRI_DEMANDEUR = "demandeur";
	/** Tri des DT par le nom du batiment du contact */
	public final static String CODE_TRI_BATIMENT = "batiment";

	/** Index de l'onglet de creation de DT */
	public final static int NUMERO_ONGLET_CREATION = 0;
	/** Index de l'onglet de consultation et de traitement de DT */
	public final static int NUMERO_ONGLET_CONSULTATION = 1;

	/** l'ordre de classement des intervenations et traitements */
	public final static String ASCENDING = "A";
	public final static String DESCENDING = "D";

	// Bookmarks

	/**
	 * Mets a jour le bookmark identifie par <code>noIndividu</code>. Retourne
	 * l'enresgistrement si tout s'est bien passe, <em>null</em> sinon.
	 */
	public CktlRecord updatePrefRechBookmark(
			Integer transId, Number noIndividu, String prbLibelle, String prbBookmark) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = fetchObject(
					ec, "PrefRechBookmark",
					newCondition("noIndividu=" + noIndividu + " and prbLibelle='" + prbLibelle + "'"));
			// On ajoute la definition si elle n'est pas encore ajoutee
			if (rec == null) {
				rec = EOUtilities.createAndInsertInstance(ec, "PrefRechBookmark");
				rec.takeStoredValueForKey(noIndividu, "noIndividu"); // PK
				rec.takeStoredValueForKey(prbLibelle, "prbLibelle"); // PK
			}
			// Ensuite, on mets a jour les informations
			if (prbBookmark != null)
				rec.takeStoredValueForKey(prbBookmark, "prbBookmark");
			if (commitECTransaction(localTransId)) {
				return (CktlRecord) rec;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Relit tous les bookmarks enregistres par l'utilisateur identifie par
	 * <code>noInvidu</code>.
	 */
	public NSArray getAllPrefRechBookmark(Number noIndividu) {
		return fetchArray("PrefRechBookmark", newCondition("noIndividu=" + noIndividu), null);
	}

	/**
	 * Supprime la definition du bookmarks <code>recBookmark</code>.
	 */
	public boolean deletePrefRechBookmark(Integer transId, CktlRecord recBookmark) {
		return (deleteFromTable(transId, "PrefRechBookmark",
				newCondition("noIndividu=" + recBookmark.intForKey("noIndividu") +
						" and prbLibelle='" + recBookmark.stringForKey("prbLibelle") + "'")) != -1);
	}

	/**
	 * Indique si l'intervenant <code>noIndividu</code> autorise ou non la
	 * publication du detail de ses traitements via le serveur de planning.
	 * Methode statique pour acces rapide a partir des DirectAction.
	 * 
	 * @param noIndividu
	 * @return
	 */
	public boolean exportPlanning(Number noIndividu) {
		return fetchObject("PrefAppli",
				newCondition("prfExportPlanning=1 and noIndividu=" + noIndividu.intValue(), null)) != null;
	}
}

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
import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

/**
 * Controlleur de l'interface HTML de creation de comptes.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class SwapCompte
		extends A_CreationSwapView {

	/** @TypeInfo java.lang.String */
	public NSMutableArray typeCompteListe;
	public String typeCompteItem;
	public String nomPersonne;
	public String prenomPersonne;
	public String dureContrat;
	public String telephone;
	public String remarques;

	/**
	 * Si ni contrat, ni categorie, alors une autre liste est proposee :
	 * stagiaires, doctotant...
	 */
	public NSArray typeAutreList;
	public String typeAutreSelectedItem;
	public String typeAutreItem;
	private final static String TYPE_AUTRE_DOCTORANT = "Doctorant";
	private final static String TYPE_AUTRE_STAGIAIRE = "Stagiaire";

	// dates de debut et de fin stage
	public NSTimestamp dateDebutStage;
	public NSTimestamp dateFinStage;

	public NSMutableArray contratTravailListe;
	public CktlRecord contratTravailItem;
	public CktlRecord contratTravailSelectedItem;

	public NSMutableArray catEmploiListe;
	public CktlRecord catEmploiItem;
	public CktlRecord catEmploiSelectedItem;

	public NSMutableArray batimentListe;
	public CktlRecord batimentItem;
	public CktlRecord batimentSelectedItem;

	public String typeCompteSelectedItem;

	public NSMutableArray serviceListe;
	public ServiceItem serviceItem;
	public ServiceItem serviceSelectedItem;
	public String typeTypeContrat; // "C" - contractuel, "T" - titulaire

	// temoin s'il faut faire une carte pro
	// public boolean isCartePro;

	public boolean errorNom;
	public boolean errorPrenom;
	public boolean errorDuree;
	public boolean errorTypeContratEmpty;
	public boolean errorTypeEmploiEmpty;
	public boolean errorTypeAutreEmpty;
	public boolean errorServiceVide;
	public boolean errorBatimentVide;
	public boolean errorDateStageEmpty;
	public boolean errorDateStage;

	public SwapCompte(WOContext context) {
		super(context);
	}

	/**
   *
   */
	public void initView() {
		remplireTypeReseau();
		remplirContrats();
		remplirBatiment();
		remplirService();
		resetView();
	}

	/*
	 * @see A_CreationSwapView#resetView()
	 */
	protected void resetView() {
		super.resetView();
		nomPersonne = StringCtrl.emptyString();
		prenomPersonne = StringCtrl.emptyString();
		dureContrat = StringCtrl.emptyString();
		telephone = StringCtrl.emptyString();
		remarques = StringCtrl.emptyString();
		clearViewErrors();
		// isCartePro = false;
	}

	/*
	 * @see A_CreationSwapView#resetDestin()
	 */
	protected void resetDestin() {
		// Pas de gestion des destinations pour ce type de vue.
	}

	protected void clearViewErrors() {
		clearErrorMessage();
		errorNom = errorPrenom = errorDuree = errorTypeContratEmpty = errorTypeEmploiEmpty = errorTypeAutreEmpty = errorServiceVide = errorBatimentVide = errorDateStageEmpty = errorDateStage = false;
	}

	public boolean hasErrors() {
		return hasMainErrors() ||
				errorNom ||
				errorPrenom ||
				errorDuree ||
				errorTypeContratEmpty ||
				errorTypeEmploiEmpty ||
				errorTypeAutreEmpty ||
				errorServiceVide ||
				errorBatimentVide ||
				errorDateStageEmpty ||
				errorDateStage;
	}

	protected String getCategorieGED() {
		return "DT_COMMUN";
	}

	/*
	 * Pas de fichier attaches avec la creation de compte.
	 */
	protected boolean ignoreFileSaveErrors() {
		return true;
	}

	/*
	 * @see A_CreationSwapView#getPostCreateWarning()
	 */
	protected String getPostCreateWarning() {
		return null;
	}

	/*
	 * @see A_CreationSwapView#attachedFilePaths()
	 */
	protected NSArray attachedFilePaths() {
		// Pas de fichiers attaches
		return new NSArray();
	}

	protected String getAttachedFilePath() {
		return StringCtrl.emptyString();
	}

	protected NSData getAttachedFileData() {
		return null;
	}

	/**
   *
   */
	private void remplireTypeReseau() {
		typeCompteListe = new NSMutableArray();
		NSArray vLans = dtDataCenter().genericBus().findVlans(Boolean.TRUE, null);
		CktlRecord rec;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < vLans.count(); i++) {
			rec = (CktlRecord) vLans.objectAtIndex(i);
			sb.setLength(0);
			sb.append(rec.stringForKey("llVlan"));
			if (!DTDataBus.isNullValue(rec.valueForKey("listeIp")))
				sb.append(" [").append(rec.valueForKey("listeIp")).append("]");
			typeCompteListe.addObject(sb.toString());
		}
		if (typeCompteListe.count() == 0)
			typeCompteListe.addObject("<VLans - aucun compte>");
		typeCompteSelectedItem = (String) typeCompteListe.objectAtIndex(0);
	}

	public void remplirContrats() {
		remplirTypeContrat();
		remplirCatEmploi();
		remplirTypeAutre();
		typeTypeContrat = "C";
		// le stage debute aujourd'hui pendant 3 mois par defaut
		dateDebutStage = DateCtrl.now();
		dateFinStage = DateCtrl.now().timestampByAddingGregorianUnits(0, 3, 0, 0, 0, 0);
	}

	/**
   *
   */
	private void remplirCatEmploi() {
		NSArray objects;
		catEmploiListe = new NSMutableArray();
		objects = dtSession().dataBus().fetchArray(
				"TypeCatEmploi", null, CktlSort.newSort("lcCategorieEmploi"));
		if (objects.count() > 0) {
			catEmploiListe.addObjectsFromArray(objects);
		}
	}

	/**
   *
   */
	private void remplirTypeContrat() {
		NSArray objects;
		contratTravailListe = new NSMutableArray();
		objects = dtSession().dataBus().fetchArray(
				"TypeContratTravail",
				DTDataBus.newCondition("((temTitulaire <> 'O') and (temRemunerationAccessoire <> 'O'))"),
				CktlSort.newSort("lcTypeContratTrav"));
		if (objects.count() > 0) {
			contratTravailListe.addObjectsFromArray(objects);
		}
	}

	/**
  *
  */
	private void remplirTypeAutre() {
		typeAutreList = new NSArray(new String[] { TYPE_AUTRE_DOCTORANT, TYPE_AUTRE_STAGIAIRE });
	}

	private void remplirService() {
		NSArray objects;
		CktlRecord rec;

		serviceListe = new NSMutableArray();
		objects = dtSession().dataCenter().serviceBus().allServices();
		if (objects.count() > 0) {
			for (int i = 0; i < objects.count(); i++) {
				rec = (CktlRecord) objects.objectAtIndex(i);
				serviceSelectedItem = new ServiceItem(StringCtrl.compactString(rec.stringForKey("llStructure"), 40,
																							" [...]") + " (" + rec.valueForKey("lcStructurePere") + ")",
																							rec.stringForKey("llStructure") + " (" + rec.valueForKey("lcStructurePere") + ")",
																							rec.stringForKey("cStructure"));
				serviceListe.addObject(serviceSelectedItem);
			}
			serviceSelectedItem = null;
		}
	}

	private void remplirBatiment() {
		NSArray objects;
		batimentListe = new NSMutableArray();
		// objects = cktlApp.dataBus().fetchArray("Batiment", null,
		// CktlSort.newSort("appellation"));
		objects = dtSession().dataCenter().contactsBus().findAllBatiments();
		if (objects.count() > 0) {
			batimentListe.addObjectsFromArray(objects);
		}
	}

	public boolean fillDataDictionary() {
		StringBuffer texte = new StringBuffer();
		StringBuffer rems = new StringBuffer();
		boolean isCDD;

		clearViewErrors();
		saveDataDico.removeAllObjects();
		parentPage().fillDataDictionary(saveDataDico);
		if (hasMainErrors()) {
			return false;
		}

		if (StringCtrl.normalize(nomPersonne).length() == 0) {
			setMainError("Le nom est absent");
			errorNom = true;
		}
		if (StringCtrl.normalize(prenomPersonne).length() == 0) {
			setMainError("Le prénom est absent");
			errorPrenom = true;
		}
		if (typeTypeContrat.equals("C") && contratTravailSelectedItem == null) {
			setMainError("Le type de contrat n'est pas renseigné");
			errorTypeContratEmpty = true;
		}
		if (typeTypeContrat.equals("T") && catEmploiSelectedItem == null) {
			setMainError("Le type d'emploi n'est pas renseigné");
			errorTypeEmploiEmpty = true;
		}
		if (typeTypeContrat.equals("A") && typeAutreSelectedItem == null) {
			setMainError("Le type n'est pas renseigné");
			errorTypeAutreEmpty = true;
		}
		isCDD = (typeTypeContrat.equals("C") && contratTravailSelectedItem != null && StringCtrl.normalize((String) contratTravailSelectedItem.valueForKey("temCdi")).equals("N"));
		if (isCDD && (StringCtrl.normalize(dureContrat).length() == 0)) {
			setMainError("La durée du contrat n'est pas renseignée");
			errorDuree = true;
		}
		if (serviceSelectedItem == null) {
			setMainError("Le service n'est pas renseigné");
			errorServiceVide = true;
		}
		if (batimentSelectedItem == null) {
			setMainError("Le batiment n'est pas renseigné");
			errorBatimentVide = true;
		}
		// controle sur les dates de stage
		if (typeTypeContrat.equals("A") && typeAutreSelectedItem != null && typeAutreSelectedItem.equals(TYPE_AUTRE_STAGIAIRE)) {
			if (dateDebutStage == null || dateFinStage == null) {
				setMainError("La date de début et/ou de fin de stage est absente");
				errorDateStageEmpty = true;
			} else if (DateCtrl.isAfter(dateDebutStage, dateFinStage)) {
				setMainError("La date de début est après la date de fin de stage");
				errorDateStage = true;
			}
		}
		if (hasErrors()) {
			return false;
		}
		// On construit d'abord le text qui sera insere comme motif
		texte.append("Demande de creation de compte :\n\n");
		texte.append("Type de compte : ").append(typeCompteSelectedItem).append("\n\n");
		texte.append("Nom    : ").append(nomPersonne).append("\n");
		texte.append("Prenom : ").append(prenomPersonne).append("\n\n");
		// texte.append("Creation d'une carte professionnelle : ").append(isCartePro
		// ? "OUI" : "NON").append("\n\n");
		texte.append("Type d'affectation : ");
		if (typeTypeContrat.equals("C")) {
			texte.append("Contractuel - ").append(contratTravailSelectedItem.valueForKey("lcTypeContratTrav"));
			if (isCDD)
				texte.append(" pour une duree de ").append(dureContrat).append(" mois");
		} else if (typeTypeContrat.equals("T")) {
			texte.append("Titulaire - ").append(catEmploiSelectedItem.valueForKey("lcCategorieEmploi"));
		} else {
			texte.append("Autre - ").append(typeAutreSelectedItem);
			if (typeAutreSelectedItem != null && typeAutreSelectedItem.equals(TYPE_AUTRE_STAGIAIRE)) {
				texte.append(" du ").append(DateCtrl.dateToString(dateDebutStage)).append(" au ").append(DateCtrl.dateToString(dateFinStage));
			}
		}
		texte.append("\n\n");
		texte.append("Service : ").append(serviceSelectedItem.nomLong).append("\n\n");
		texte.append("Localisation : ").append(batimentSelectedItem.valueForKey("appellation"));
		texte.append("\n\n");
		texte.append("Telephone : ").append(StringCtrl.normalize(telephone)).append("\n\n");
		if (StringCtrl.normalize(remarques).length() > 0) {
			rems.append("Remarques :\n");
			rems.append(StringCtrl.quoteText(remarques, "  ")).append("\n\n");
		}
		if (!hasErrors()) {
			// Teste si le taille des remarques ne depasse pas de la taille
			// maximale autorisee des commentaires
			if (!dtSession().dtDataBus().checkForMaxSize("Intervention", "intMotif",
																			rems.toString(), "Remarques",
																			texte.length(), true, true)) {
				setMainError(dtSession().dtDataBus().getErrorMessage());
				return false;
			}
		}
		if (!hasErrors()) {
			saveDataDico.setObjectForKey(DateCtrl.now().timestampByAddingGregorianUnits(0, 0, 10, 0, 0, 0), "intDateSouhaite");
			saveDataDico.setObjectForKey(texte.append(rems).toString(), "intMotif");
			// En plus, on change ceci, par rapport a la definition par defaut
			saveDataDico.setObjectForKey(cktlSession().connectedUserInfo().noIndividu(), "intNoIndConcerne");
			saveDataDico.setObjectForKey(cktlSession().connectedUserInfo().noIndividu(), "intNoIndAppelant");
			saveDataDico.setObjectForKey(cktlSession().connectedUserInfo().email(), "mailIndConcerne");
			saveDataDico.setObjectForKey(cktlSession().connectedUserInfo().email(), "mailIndAppelant");
			saveDataDico.setObjectForKey(" : creation de compte", "mailSubject");
			saveDataDico.setObjectForKey(DTDataBus.nullValue(), "lolfId");
			return true;
		} else {
			return false;
		}
	}

	public boolean createDevis() {
		clearViewErrors();
		// Rien pour ce type des demandes
		return false;
	}

	public void sendMail(NSArray<String> filesURL) {
		formatAndSendMail(filesURL);
	}

	public WOComponent validerDemande() {
		return super.validerDemande();
	}

	public WOComponent nouvelleDemande() {
		return super.nouvelleDemande();
	}

	private class ServiceItem {
		public String nomCourt;
		public String nomLong;
		public String cStructure;

		public ServiceItem(String newNomCourt, String newNomLong, String newCStructure) {
			nomCourt = newNomCourt;
			nomLong = newNomLong;
			cStructure = newCStructure;
		}
	}

	public boolean mustCreateDevis() {
		return false;
	}

	/**
	 * On ne traite pas les informations financières pour la création de compte
	 * informatique
	 */
	public boolean shouldProcessDataInfin() {
		return false;
	}

	/**
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return I_Swap.SWAP_VIEW_COMPTE_ID;
	}

	/**
	 * @see A_CreationSwapView#getDestinationLolfCtrl()
	 */
	@Override
	public A_ListeDestinationLolfCtrl getDestinationLolfCtrl() {
		return null;
	}
}

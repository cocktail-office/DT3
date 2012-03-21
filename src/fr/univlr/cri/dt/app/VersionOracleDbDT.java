/*
 * Copyright Universit� de La Rochelle 2007
 *
 * Ce logiciel est un programme informatique servant � g�rer les demandes
 * d'utilisateurs aupr�s d'un service.
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.

 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 

 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */
package fr.univlr.cri.dt.app;

import org.cocktail.fwkcktlwebapp.server.version.CktlVersionOracleUser;

/**
 * Classe de controle de la version du user Oracle de DT
 *
 * @author ctarade
 */
public class VersionOracleDbDT extends CktlVersionOracleUser {

  // nom de ce qui est controle (pour affichage dans les logs)
  public String name() {
    return "DT3 ORACLE user";
  }

  // la table contenant la liste des versions de user
  public String dbUserTableName() {	
    return "DT3.DB_VERSION";
  }
  
  // l'attribut contenant la date de la version
  public String dbVersionDateColumnName() {
    return "DB_DATE";
  }
  
  // l'attribut contenant l'id de la version
  public String dbVersionIdColumnName(){
    return "DB_VERSION";
  }

  // mettre ici les dependances du user (version oracle, autres version users ...)
  public CktlVersionRequirements[] dependencies() {
    return null;
  }
}
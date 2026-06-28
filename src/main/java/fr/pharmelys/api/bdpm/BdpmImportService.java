package fr.pharmelys.api.bdpm;

public interface BdpmImportService {

    /**
     * Lance les 4 imports, chacun isolé : une erreur sur l'un ne bloque pas les
     * autres.
     */
    void importAll();

    void importMedications();

    void importCompositions();

    void importGenericGroups();

    void importShortages();
}
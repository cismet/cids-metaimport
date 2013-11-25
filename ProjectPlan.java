import de.cismet.jpresso.core.execution.ProjectPlanBase;
import de.cismet.jpresso.core.serviceprovider.exceptions.JPressoException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectPlan extends ProjectPlanBase {

    public void start() {
        try {
            // In cids we use negative foreign key references for 1-n relationships.
            // This negative foreign keys are problematic for the import run.
            // That's why whe have to add two temporary rows in the table cs_attr
            // of the SOURCE database (will be dropped later database).

            // The field "foreign_key_references_to_1n" is a boolean that
            // indicates if the relationship is of the 1-n type (= negative foreign key).
            // The field "foreign_key_references_to_abs" contains the absolute
            // value of foreign_key_references.
            EXECUTOR.execute("cs_attr_add_foreign_key_1n_source.rqs");

            // In the target database, whe have to remember if a foreign key is
            // of the 1-n type, so that we can set the negative value after the import run.
            // That's why we have to add the "foreign_key_references_to_1n" field there too.
            EXECUTOR.execute("cs_attr_add_foreign_key_1n_target.rqs");

            // ====

            // import of the domain, usergroup, user, and membership data.
            EXECUTOR.execute("cs_domain.run");
            EXECUTOR.execute("cs_ug.run");
            EXECUTOR.execute("cs_usr.run");
            EXECUTOR.execute("cs_ug_membership.run");

            // import of the type, class and attribute data (with their permissions).
            EXECUTOR.execute("cs_type_class_attr.run");
            EXECUTOR.execute("cs_ug_attr_perm.run");
            EXECUTOR.execute("cs_ug_class_perm.run");

            // import of the catalogue-node data (with their permissions).
            EXECUTOR.execute("cs_cat_node.run");
            EXECUTOR.execute("cs_ug_cat_node_perm.run");

            // import of the configuration attributes data (with their permissions).
            EXECUTOR.execute("cs_config_attr.run");

            // fixes the sequences for the cids tables
            EXECUTOR.execute("update_sequences.rqs");
        } catch (JPressoException ex) {
            Logger.getLogger(ProjectPlan.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

                // Replaces the foreign values with the same negative values
                // where the "foreign_key_references_to_1n" is true.
                // Drops the "foreign_key_references_to_1n" field (not needed anymore)
                EXECUTOR.execute("cs_attr_remove_foreign_key_1n_target.rqs");
            } catch (JPressoException ex) {
                Logger.getLogger(ProjectPlan.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                // Drops the "foreign_key_references_to_1n" and the
                // "foreign_key_references_to_abs" from the cs_attr table
                // of the SOURCE database
                EXECUTOR.execute("cs_attr_remove_foreign_key_1n_source.rqs");
            } catch (JPressoException ex) {
                Logger.getLogger(ProjectPlan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}


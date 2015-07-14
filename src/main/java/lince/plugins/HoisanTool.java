package lince.plugins;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import lince.modelo.InstrumentoObservacional.*;
import lince.modelo.Registro;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Alberto Soto on 08/06/2015.
 */
public class HoisanTool {

   private Logger log = Logger.getLogger(HoisanTool.class.getName());
   private static final String HOISAN_TEMPLATE_FILE = "template/hoisanTemplate.mdb";
   private static final int FRAME_WINDOW = 40;

   /**
    * @param file
    * @return
    */
   public boolean importFile(File file) {
      if (file != null) {
         try {
            Database db = Database.open(file);
            Table criterios = db.getTable(HoisanVars.CRITERIA_TABLE_NAME.toString());
            Table categorias = db.getTable(HoisanVars.CATEGORY_TABLE_NAME.toString());
            Table tiempos = db.getTable(HoisanVars.OBSERVATION_TIMES_TABLE_NAME.toString());
            Table tiempoCategorias = db.getTable(HoisanVars.OBSERVATION_CATEGORY_TABLE_NAME.toString());
            InstrumentoObservacional instrumentoObservacional = generateInstrumentObservacional(criterios, categorias);
            Map<String, Registro> registros = generateRegistros(tiempos, tiempoCategorias, criterios, categorias, instrumentoObservacional);
            File parentFile = file.getParentFile();
            //generamos una carpeta a mismo nivel de fichero seleccionado
            File linceDir = getFreeFile(parentFile, "Lince", "");
            linceDir.mkdir();
            //guardamos el instrumento observacional en el fichero asociado
            instrumentoObservacional.setPath(getFreeFile(linceDir, file.getName(), ".ilince"));
            instrumentoObservacional.save();
            //generamos un nuevo fichero de registro para cada uno de los registros detectados
            for (Map.Entry<String, Registro> row : registros.entrySet()) {
               Registro registro = row.getValue();
               registro.setPath(getFreeFile(linceDir, new File(row.getKey()).getName(), ".rlince"));
               registro.save();
            }
         } catch (IOException ex) {
            log.error("Importing Hoisan File", ex);
            return false;
         }
      }
      return true;
   }

   /**
    * @param outputFile
    * @return
    */
   public boolean exportFile(File outputFile) {
      try {
         File realBaseFile = createValidEmptyHoisanFile(outputFile);
         //tablas vacias
         Database db = Database.open(realBaseFile);
         Table tCriterios = db.getTable(HoisanVars.CRITERIA_TABLE_NAME.toString());
         Table tCategorias = db.getTable(HoisanVars.CATEGORY_TABLE_NAME.toString());
         Table tTiempos = db.getTable(HoisanVars.OBSERVATION_TIMES_TABLE_NAME.toString());
         Table tTiempoCategorias = db.getTable(HoisanVars.OBSERVATION_CATEGORY_TABLE_NAME.toString());

         //usado para obtener cuadros de dialogo en logica brais
         String[] otros = {"TFrames", "DuraciónFr", "TSegundos", "DuraciónSeg", "TMilisegundos", "DuraciónMiliseg"};
         //desde criterios tenemos toda la jerarquia de elementos a insertar en tables criterio y categoria
         List<Criterio> criterios = Arrays.asList(InstrumentoObservacional.getInstance().getCriterios());
         List<NodoInformacion> datosMixtos = Arrays.asList(InstrumentoObservacional.getInstance().getDatosMixtos());
         List<NodoInformacion> datosFijos = Arrays.asList(InstrumentoObservacional.getInstance().getDatosFijos());
         Object lista[] = new Object[criterios.size() + otros.length + datosMixtos.size() + datosFijos.size()];
         //usado para exportar e importar en codigo brais
         //en registro -> datos variables, tendriamos todos los datos necesarios para el registro temporal
         Registro registro = Registro.getInstance();
         //tCriterios.addRow(Column.AUTO_NUMBER,"tst","Lince","loren ipsum")
         storeCriteria(db, tCriterios, criterios, tCategorias);
         return true;
      } catch (Exception e) {
         log.error("Error copying file ", e);
      }
      return false;
   }


   private void storeCriteria(Database db, Table data, List<Criterio> innerData, Table tCategories) {

      for (Criterio criterio : innerData) {
         try {
            data.addRow(Column.AUTO_NUMBER, criterio.getNombre(), "Lince", criterio.getDescripcion());
            Integer id = findCriteriaByName(data, criterio.getNombre());
            db.flush();
            for (Categoria cat : criterio.getCategoriasHijo()) {
               //no entiendo pq va con doble id, pero asi funciona correctamente
               //si no, da number format exception
               tCategories.addRow(Column.AUTO_NUMBER
                       , id
                       , id
                       , StringUtils.substring(cat.getNombre(), 0, 49)
                       , cat.getDescripcion()
                       , cat.getCodigo());
               db.flush();
            }
         } catch (Exception e) {
            log.error("storing criteria");
         }
      }
      try {
         db.flush();
      } catch (Exception e) {
         log.error("storing criteria -final commit");
      }


   }

   /**
    * Creates on selected File a valid Hoisan Empty File
    *
    * @param outputFile
    * @throws IOException
    */
   private File createValidEmptyHoisanFile(File outputFile) throws IOException {
      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(classLoader.getResource(HOISAN_TEMPLATE_FILE).getFile());
      FileUtils.copyFile(file, outputFile);
      File rtn = new File(StringUtils.substringBeforeLast(outputFile.getPath(), ".") + ".mdb");
      outputFile.renameTo(rtn);
      return rtn;
   }


   /**
    * Old function for managing existing files
    *
    * @param dir
    * @param name
    * @param extension
    * @return
    */
   private File getFreeFile(File dir, String name, String extension) {
      File f = new File(dir, name + extension);
      int i = 2;
      while (f.exists()) {
         f = new File(dir, name + " (" + i++ + ")" + extension);
      }
      return f;
   }

   /**
    * @param criterios
    * @param categorias
    * @return
    */
   private InstrumentoObservacional generateInstrumentObservacional(Table criterios, Table categorias) {
      InstrumentoObservacional.loadNewInstance();
      InstrumentoObservacional instrumentoObservacional = InstrumentoObservacional.getInstance();
      RootInstrumentoObservacional root = (RootInstrumentoObservacional) instrumentoObservacional.getModel().getRoot();
      DefaultMutableTreeNode fijo = (DefaultMutableTreeNode) root.getChildAt(0);
      DefaultMutableTreeNode variable = (DefaultMutableTreeNode) root.getChildAt(2);

      for (Map<String, Object> row : criterios) {
         String type = HoisanVars.CRITERIA_TYPE.getStringValue(row);
         if (HoisanVars.CRITERIA_TYPE_FIXED.equals(type)) {
            //type.equals("Fijo")) {
            InstrumentoObservacional.getInstance().addHijo(fijo
                    , HoisanVars.CRITERIA_NAME.getStringValue(row));
            NodoInformacion[] datos = instrumentoObservacional.getDatosFijos();
            datos[datos.length - 1].setDescripcion(HoisanVars.CRITERIA_DESCRIPTION.getStringValue(row));
         } else if (HoisanVars.CRITERIA_TYPE_NORMAL.equals(type)) {
            //(type.equals("Normal")) {
            InstrumentoObservacional.getInstance().addHijo(variable, HoisanVars.CRITERIA_NAME.getStringValue(row));
            Criterio[] cs = instrumentoObservacional.getCriterios();
            Criterio criterio = cs[cs.length - 1];
            criterio.setDescripcion(HoisanVars.CRITERIA_DESCRIPTION.getStringValue(row));
            generateCategorias(criterio, HoisanVars.CRITERIA_ID.getIntValue(row), categorias);
         } else {
            // Ignore
         }
      }
      return instrumentoObservacional;
   }

   /**
    * @param criterio
    * @param criterioId
    * @param categorias
    */
   private void generateCategorias(Criterio criterio, Integer criterioId, Table categorias) {
      for (Map<String, Object> row : categorias) {
         String currentCriteriaFK = HoisanVars.CATEGORY_CRITERIA_ID.getStringValue(row);
         if (currentCriteriaFK.equals(criterioId)) {
            InstrumentoObservacional.getInstance().addHijo(criterio, HoisanVars.CATEGORY_DESCRIPTION.getStringValue(row));
            Categoria categoria = (Categoria) criterio.getChildAt(criterio.getChildCount() - 1);
            categoria.setCodigo(HoisanVars.CATEGORY_NAME.getStringValue(row));
            categoria.setDescripcion(HoisanVars.CATEGORY_CORE.getStringValue(row));
         }
      }
   }

   /**
    * @param tiempos
    * @param tiempoCategorias
    * @param criterios
    * @param categorias
    * @param instrumentoObservacional
    * @return
    */
   private Map<String, Registro> generateRegistros(Table tiempos, Table tiempoCategorias, Table criterios, Table categorias, InstrumentoObservacional instrumentoObservacional) {
      Map<String, Registro> registros = new HashMap<String, Registro>();
      for (Map<String, Object> row : tiempos) {
         String nombre = HoisanVars.TIME_VIDEO_NAME.getStringValue(row);
         Integer timeID = HoisanVars.TIME_ID.getIntValue(row);
         Integer initialFrame = HoisanVars.TIME_INITIAL_FRAME.getIntValue(row);
         Registro registro = registros.get(nombre);
         if (registros.get(nombre) == null) {
            Registro.loadNewInstance();
            registro = Registro.getInstance();
            registros.put(nombre, registro);
         }
         Map<Criterio, Categoria> registerItem = generateRegisterRow(timeID
                 , tiempoCategorias
                 , criterios
                 , categorias
                 , instrumentoObservacional.getCriterios());
         registro.addRow(initialFrame * FRAME_WINDOW
                 , registerItem
                 , new HashMap<NodoInformacion, String>());
      }
      return registros;
   }

   /**
    * @param timeId
    * @param tiempoCategorias
    * @param criterioTable
    * @param categoriaTable
    * @param criterios
    * @return
    */
   private Map<Criterio, Categoria> generateRegisterRow(Integer timeId, Table tiempoCategorias, Table criterioTable, Table categoriaTable, Criterio criterios[]) {
      Map<Criterio, Categoria> rtn = new HashMap<Criterio, Categoria>();
      for (Map<String, Object> row : tiempoCategorias) {
         final Integer currentCriteriaID = HoisanVars.CRITERIA_ID.getIntValue(row);
         final Integer currentCategoryID = HoisanVars.CATEGORY_ID.getIntValue(row);
         final Integer currentTimeID = HoisanVars.TIME_ID.getIntValue(row);
         if (currentTimeID.equals(timeId)) {
            Criterio criteria = findCriterio(criterios, criterioTable, currentCriteriaID);
            if (criteria != null) { // TODO registrar los fijos
               Categoria ca = findCategory(criteria, categoriaTable, currentCategoryID);
               rtn.put(criteria, ca);
            }
         }
      }
      return rtn;
   }

   /**
    * @param cs
    * @param criterios
    * @param id
    * @return
    */
   private Criterio findCriterio(Criterio cs[], Table criterios, int id) {
      String nombre = findCriteriaById(criterios, id);
      for (Criterio c : cs) {
         if (StringUtils.equals(c.getNombre(), nombre)) {
            return c;
         }
      }
      return null;
   }

   /**
    * @param c
    * @param categoriaTable
    * @param id
    * @return
    */
   private Categoria findCategory(Criterio c, Table categoriaTable, Integer id) {
      String code = findCategoryById(categoriaTable, id);
      return c.getCategoriaByCodigo(code);
   }


   /**
    * Devuelve la etiqueta de un id para criterios
    *
    * @param criteriaTable
    * @param id
    * @return
    */
   private String findCriteriaById(Table criteriaTable, int id) {
      return findFieldWithValue(criteriaTable, id, HoisanVars.CRITERIA_ID, HoisanVars.CRITERIA_NAME);
   }

   private Integer findCriteriaByName(Table criteriaTable, String name) {
      return findFieldWithValue(criteriaTable, name, HoisanVars.CRITERIA_NAME, HoisanVars.CRITERIA_ID);
   }

   /**
    * Devuelve la etiqueta de un id para categorias
    *
    * @param categoriaTable
    * @param id
    * @return
    */
   private String findCategoryById(Table categoriaTable, Integer id) {
      return findFieldWithValue(categoriaTable, id, HoisanVars.CATEGORY_ID, HoisanVars.CATEGORY_NAME);
   }

   /**
    * Metodo generico para buscar una etiqueta en una table
    *
    * @param table
    * @param id
    * @param idField
    * @param labelField
    * @return
    */
   private <T> T findFieldWithValue(Table table, Object id, HoisanVars idField, HoisanVars labelField) {
      try {
         for (Map<String, Object> row : table) {
            if (row.get(idField.toString()).equals(id)) {
               return (T) row.get(labelField.toString());
            }
         }
      } catch (Exception e) {
         log.error(e);
      }
      return null;
   }
}

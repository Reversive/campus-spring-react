package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.FileDao;
import ar.edu.itba.paw.models.FileCategory;
import ar.edu.itba.paw.models.FileExtensionModel;
import ar.edu.itba.paw.models.FileModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:schema.sql")
public class FileDaoImplTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private FileDao fileDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert JdbcInsert;

    private static final RowMapper<FileModel> FILE_MODEL_ROW_MAPPER = (rs, rowNum) -> {
        return new FileModel(rs.getInt("fileId"), rs.getLong("fileSize"), new FileCategory(rs.getLong("categoryId"), rs.getString("categoryName")), rs.getString("fileName"), rs.getDate("fileDate"), rs.getObject("file", byte[].class), new FileExtensionModel(rs.getLong("fileExtensionId"),rs.getString("fileExtension")));
    };

    // FileCategory
    private final int CATEGORY_ID = 1;
    private final String CATEGORY_NAME = "Guia Practica";

    // FileExtension
    private final int FILE_EXTENSION_ID = 1;
    private final String FILE_EXTENSION = "pdf";

    // FileModel
    private final int FILE_ID = 1;

    private FileModel createFileModelObject() throws FileNotFoundException {
        FileCategory fCategory = new FileCategory(CATEGORY_ID,CATEGORY_NAME);
        FileExtensionModel fExtension = new FileExtensionModel(FILE_EXTENSION_ID,FILE_EXTENSION);
        FileModel fModel = new FileModel();
        fModel.setCategory(fCategory);
        fModel.setExtension(fExtension);
        fModel.setFileId(FILE_ID);

        String filePath = "C:/Users/Tomas/Downloads/Tp04 Lenguajes Regulares Expresiones Regulares.pdf";
        File fileInFileSystem = new File(filePath);

        fModel.setName(fileInFileSystem.getName());

        ////////////
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        byte[] buffer = new byte[0];
        try {
            buffer = new byte[(int)fileInFileSystem.length()];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(fileInFileSystem);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        ///////////

        fModel.setSize(buffer.length);
        fModel.setFile(buffer);

        LocalDate currentTime = java.time.LocalDate.now();
        Date currentTimeDate = Date.from(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant());
        fModel.setDate(currentTimeDate);

        return fModel;
    }

    private void insertFileModelToDB(FileModel fModel){
        Map<String, Object> args = new HashMap<>();
        args.put("fileExtensionId",fModel.getExtension().getFileExtensionId());
        args.put("categoryId",fModel.getCategory().getCategoryId());
        args.put("fileSize",fModel.getSize());
        args.put("fileDate",fModel.getDate());
        args.put("fileName",fModel.getName());
        args.put("file",fModel.getFile());
        args.put("fileId", FILE_ID);
        JdbcInsert.execute(args);
    }

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("files");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "file_categories");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "file_extensions");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "files");
        jdbcTemplate.execute(String.format("INSERT INTO file_categories VALUES (%d, '%s')",CATEGORY_ID, CATEGORY_NAME));
        jdbcTemplate.execute(String.format("INSERT INTO file_extensions VALUES (%d, '%s')",FILE_EXTENSION_ID, FILE_EXTENSION));
    }

    @Test
    public void testCreate() throws FileNotFoundException {
        FileModel fileModel = fileDao.create(createFileModelObject());
        assertNotNull(fileModel);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "files"));
    }

    @Test
    public void testDelete() throws FileNotFoundException {
        insertFileModelToDB(createFileModelObject());
        fileDao.delete(FILE_ID);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "files"));
    }

    @Test(expected = AssertionError.class)
    public void testDeleteNoExist() throws FileNotFoundException {
        insertFileModelToDB(createFileModelObject());
        final boolean isDeleted = fileDao.delete(FILE_ID + 1);
        Assert.fail("Should have thrown assertion error for non-existent key 'file id' ");
        assertFalse(isDeleted);
    }

    @Test
    public void testGetById() throws FileNotFoundException {
        FileModel fModel = createFileModelObject();
        insertFileModelToDB(fModel);

        Optional<FileModel> fileFromDB = fileDao.getById(FILE_ID);
        assertTrue(fileFromDB.isPresent());
        assertEquals(FILE_ID, fileFromDB.get().getFileId());
        assertEquals(FILE_EXTENSION_ID, fileFromDB.get().getExtension().getFileExtensionId());
        assertEquals(CATEGORY_ID, fileFromDB.get().getCategory().getCategoryId());
        assertEquals(fModel.getSize(), fileFromDB.get().getSize());
        assertEquals(fModel.getName(), fileFromDB.get().getName());
        assertEquals(fModel.getDate(), fileFromDB.get().getDate());
        assertArrayEquals(fModel.getFile(), fileFromDB.get().getFile());

    }

    @Test(expected = AssertionError.class)
    public void testGetByIdNoExist() throws FileNotFoundException {
        FileModel fModel = createFileModelObject();
        insertFileModelToDB(fModel);

        Optional<FileModel> fileFromDB = fileDao.getById(FILE_ID+1);
        Assert.fail("Should have thrown assertion error for non-existent key 'file id' ");
        assertFalse(fileFromDB.isPresent());
    }

    @Test
    public void testList() throws FileNotFoundException {
        insertFileModelToDB(createFileModelObject());
        List<FileModel> list = fileDao.list();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(FILE_ID, list.get(0).getFileId());
    }

    @Test
    public void testUpdate() throws FileNotFoundException {
        FileModel fModel = createFileModelObject();
        insertFileModelToDB(fModel);
        fModel.setName("nuevoNombre");
        final boolean isUpdated = fileDao.update(FILE_ID, fModel);
        assertTrue(isUpdated);

        String sqlGetFileOfId = String.format("SELECT * FROM files NATURAL JOIN file_extensions NATURAL JOIN file_categories WHERE fileId = %d;", FILE_ID);
        FileModel fileDB = jdbcTemplate.query(sqlGetFileOfId,FILE_MODEL_ROW_MAPPER).get(0);

        assertEquals(FILE_ID, fileDB.getFileId());
        assertEquals(CATEGORY_ID, fileDB.getCategory().getCategoryId());
        assertEquals(FILE_EXTENSION_ID, fileDB.getExtension().getFileExtensionId());
        assertEquals("nuevoNombre", fileDB.getName());
    }

}

package integration;

import java.util.Random;

import omero.model.Channel;
import omero.model.Dataset;
import omero.model.Folder;
import omero.model.Image;
import omero.model.ImportJob;
import omero.model.ImportJobI;
import omero.model.JobStatus;
import omero.model.TagAnnotation;
import omero.model.TagAnnotationI;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ome.api.JobHandle;

public class ObjectPropertiesTest extends AbstractServerTest {


    /* method for creating long (1MB) strings for names of objects */
    private String createName(int integer) throws Exception {
        final Random rng = new Random();
        final char[] chars = new char[integer];  // string length
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) ('a' + rng.nextInt(26));
        }
        final String string = new String(chars);
        return string;
    }

    /* method for setting up the testing user and group
     * to run before every test */
    @BeforeMethod
    private void createUserAndGroup() throws Exception {
        newUserAndGroup("rw----");
    }

    /**
     * Test to create a (tag) annotation and save it with long name
     * and long namespace
     *
     * @throws Exception
     *             Thrown if an error occurred.
     */
    @Test
    public void testAnnotationNameAndNsSaving() throws Exception {
        /* Tag annotation is used here as a good representative to test
         * the annotations in general */
        final TagAnnotation ann = new TagAnnotationI();
        /* creates name with lenght in approx. bytes, for annotation name
         * and ann namespace sizes of >4K the test fails, needs to be investigated */
        final String name = createName(2000);
        final String namespace = createName(2000);
        ann.setName(omero.rtypes.rstring(name));
        ann.setNs(omero.rtypes.rstring(namespace));;
        TagAnnotation sent = (TagAnnotation) iUpdate.saveAndReturnObject(ann);
        String savedName = sent.getName().getValue().toString();
        String savedNamespace = sent.getNs().getValue().toString();
        long id = sent.getId().getValue();
        final TagAnnotation retrievedAnnotation = (TagAnnotation) iQuery.get("TagAnnotation", id);
        final String retrievedName = retrievedAnnotation.getName().getValue().toString();
        final String retrievedNamespace = retrievedAnnotation.getNs().getValue().toString();
        Assert.assertEquals(name, retrievedName, savedName);
        Assert.assertEquals(namespace, retrievedNamespace, savedNamespace);
    }

    /**
     * Test to create a channel and save it with long lookup table
     *
     * @throws Exception
     *             Thrown if an error occurred.
     */
    @Test
    public void testChannelLUT() throws Exception {
        /* create some image which contains a valid channel */
        final Image img = mmFactory.createImage();
        final Image sentImage = (Image) iUpdate.saveAndReturnObject(img);
        /* get the channel from the image */
        final Channel ch = sentImage.getPrimaryPixels().getChannel(0);
        final String lut = createName(1000000);
        ch.setLookupTable(omero.rtypes.rstring(lut));
        Channel sent = (Channel) iUpdate.saveAndReturnObject(ch);
        String savedLut = sent.getLookupTable().getValue().toString();
        long id = sent.getId().getValue();
        final Channel retrievedChannel = (Channel) iQuery.get("Channel", id);
        final String retrievedLut = retrievedChannel.getLookupTable().getValue().toString();
        Assert.assertEquals(lut, retrievedLut, savedLut);
    }

    /**
     * Test to create a dataset and save it with long name
     *
     * @throws Exception
     *             Thrown if an error occurred.
     */
    @Test
    public void testDatasetNameSaving() throws Exception {
        Dataset dat = mmFactory.simpleDataset();
        final String name = createName(1000000);
        dat.setName(omero.rtypes.rstring(name));
        Dataset sent = (Dataset) iUpdate.saveAndReturnObject(dat);
        String savedName = sent.getName().getValue().toString();
        long id = sent.getId().getValue();
        final Dataset retrievedDataset = (Dataset) iQuery.get("Dataset", id);
        final String retrievedName = retrievedDataset.getName().getValue().toString();
        Assert.assertEquals(name, retrievedName, savedName);
    }

    /**
     * Test to create a folder and save it with long name
     *
     * @throws Exception
     *             Thrown if an error occurred.
     */
    @Test
    public void testFolderNameSaving() throws Exception {
        Folder dat = mmFactory.simpleFolder();
        final String name = createName(1000000);
        dat.setName(omero.rtypes.rstring(name));
        Folder sent = (Folder) iUpdate.saveAndReturnObject(dat);
        String savedName = sent.getName().getValue().toString();
        long id = sent.getId().getValue();
        final Folder retrievedDataset = (Folder) iQuery.get("Folder", id);
        final String retrievedName = retrievedDataset.getName().getValue().toString();
        Assert.assertEquals(name, retrievedName, savedName);
    }

    /**
     * Test to create an image and save it with long name
     *
     * @throws Exception
     *             Thrown if an error occurred.
     */
    @Test
    public void testImageNameSaving() throws Exception {
        Image img = mmFactory.simpleImage();
        final String name = createName(1000000);
        img.setName(omero.rtypes.rstring(name));
        Image sent = (Image) iUpdate.saveAndReturnObject(img);
        String savedName = sent.getName().getValue().toString();
        long id = sent.getId().getValue();
        final Image retrievedImage = (Image) iQuery.get("Image", id);
        final String retrievedName = retrievedImage.getName().getValue().toString();
        Assert.assertEquals(name, retrievedName, savedName);
    }

    /**
     * Test to create a long import job image name and save it
     *
     * @throws Exception
     *             Thrown if an error occurred.
     */
    @Test
    public void testImportJobImageNameAndDescrSaving() throws Exception {
        logRootIntoGroup();
        final ImportJob importJob = new ImportJobI();
        importJob.setGroupname(omero.rtypes.rstring("GroupName"));
        importJob.setMessage(omero.rtypes.rstring("message"));
        importJob.setScheduledFor(omero.rtypes.rtime(System.currentTimeMillis()));
        importJob.setStatus((JobStatus) factory.getTypesService().getEnumeration(JobStatus.class.getName(), JobHandle.FINISHED));
        importJob.setSubmitted(omero.rtypes.rtime(System.currentTimeMillis()));
        importJob.setType(omero.rtypes.rstring("Test"));
        importJob.setUsername(omero.rtypes.rstring("username"));

        final String name = createName(1000000);
        importJob.setImageName(omero.rtypes.rstring(name));
        final String desc = createName(1000000);
        importJob.setImageDescription(omero.rtypes.rstring(desc));
        ImportJob sent = (ImportJob) iUpdate.saveAndReturnObject(importJob);
        final String savedName = sent.getImageName().getValue().toString();
        final String savedDesc = sent.getImageDescription().getValue().toString();
        final long id = sent.getId().getValue();
        final ImportJob retrievedImportJob = (ImportJob) iQuery.get("ImportJob", id);
        final String retrievedName = retrievedImportJob.getImageName().getValue().toString();
        final String retrievedDesc = retrievedImportJob.getImageDescription().getValue().toString();
        Assert.assertEquals(name, retrievedName, savedName);
        Assert.assertEquals(desc, retrievedDesc, savedDesc);
    }
}

package ca.ubc.cs.cpsc211.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.ubc.cs.cpsc211.photo.DuplicateTagException;
import ca.ubc.cs.cpsc211.photo.Tag;
import ca.ubc.cs.cpsc211.photo.TagManager;

/**
 * Tests basic operations of TagManager
 */

public class TagManagerTest {
	
	TagManager aTagManager;

	@Before
	public void setUp() throws Exception {
		aTagManager = new TagManager();
	}

	@Test
	public void testCreateUniqueTags() throws DuplicateTagException {
		aTagManager.createTag("birthday");
		aTagManager.createTag("AuntBetty");
		assertEquals(2, aTagManager.getTags().size());
		assertNotNull(aTagManager.findTag("birthday"));
		assertNotNull(aTagManager.findTag("AuntBetty"));
	}
	
	@Test(expected=DuplicateTagException.class) 
	public void testCreateDuplicateTag() throws DuplicateTagException {
		aTagManager.createTag("birthday");
		aTagManager.createTag("birthday");
	}
	
	@Test 
	public void testRemoveTag() throws DuplicateTagException {
		aTagManager.createTag("birthday");
		aTagManager.createTag("AuntBetty");
		assertEquals(2, aTagManager.getTags().size());
		assertTrue(aTagManager.removeTag("birthday"));
		assertEquals(1, aTagManager.getTags().size());
		assertNotNull(aTagManager.findTag("AuntBetty"));
	}
	
	@Test
	public void testRemoveTagTwice() throws DuplicateTagException {
		aTagManager.createTag("birthday");
		aTagManager.removeTag("birthday");
		assertFalse(aTagManager.removeTag("birthday"));
	}

	@Test
	public void renameTag() throws DuplicateTagException {
		aTagManager.createTag("birthday");
		Tag birthdayTag = aTagManager.findTag("birthday");
		aTagManager.renameTag("birthday", "newBirthday");
		assertNull(aTagManager.findTag("birthday"));
		assertNotNull(aTagManager.findTag("newBirthday"));
		assertEquals(birthdayTag, aTagManager.findTag("newBirthday"));
		aTagManager.renameTag("newBirthday", "birthday");
	}
	
	@Test(expected=DuplicateTagException.class)
	public void renameTagToDuplicateName() throws DuplicateTagException {
		aTagManager.createTag("birthday");
		aTagManager.createTag("birthday");
	}
	
}

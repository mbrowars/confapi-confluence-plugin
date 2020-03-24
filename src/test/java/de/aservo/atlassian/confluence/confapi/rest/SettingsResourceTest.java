package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.confluence.settings.setup.DefaultTestSettings;
import com.atlassian.confluence.settings.setup.OtherTestSettings;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.SettingsBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static de.aservo.atlassian.confapi.junit.ResourceAssert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsResourceTest {

    @Mock
    private SettingsManager settingsManager;

    private SettingsResource settingsResource;

    @Before
    public void setup() {
        settingsResource = new SettingsResource(settingsManager);

    }@Test
    public void testResourcePath() {
        assertResourcePath(settingsResource, ConfAPI.SETTINGS);
    }

    @Test
    public void testGetSettingsPath() {
        assertResourceMethodGetNoSubPath(settingsResource, "getSettings");
    }

    @Test
    public void testGetSettings() {
        final Settings settings = new DefaultTestSettings();
        doReturn(settings).when(settingsManager).getGlobalSettings();

        final Response response = settingsResource.getSettings();
        final SettingsBean bean = (SettingsBean) response.getEntity();

        assertEquals(new SettingsBean(settings.getBaseUrl(), settings.getSiteTitle()), bean);
    }

    @Test
    public void testPutSettingsPath() {
        assertResourceMethodPutNoSubPath(settingsResource, "putSettings", SettingsBean.class);
    }

    @Test
    public void testPutSettings() {
        final Settings defaultSettings = new DefaultTestSettings();
        doReturn(defaultSettings).when(settingsManager).getGlobalSettings();

        final Settings updateSettings = new OtherTestSettings();
        final SettingsResource resource = new SettingsResource(settingsManager);
        final SettingsBean requestBean = new SettingsBean(updateSettings.getBaseUrl(), updateSettings.getSiteTitle());
        final Response response = resource.putSettings(requestBean);
        final SettingsBean responseBean = (SettingsBean) response.getEntity();

        final ArgumentCaptor<Settings> settingsCaptor = ArgumentCaptor.forClass(Settings.class);
        verify(settingsManager).updateGlobalSettings(settingsCaptor.capture());
        final Settings settings = settingsCaptor.getValue();

        assertEquals(requestBean, new SettingsBean(settings.getBaseUrl(), settings.getSiteTitle()));
        assertEquals(requestBean, responseBean);
    }

}
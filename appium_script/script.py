import unittest
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time



capabilities = dict(
    platformName='Android',
    automationName='uiautomator2',
    deviceName='emulator-5554', # Replace with your device name : run adb devices to get the device name
    appPackage='com.sdapps.auraascend',
    appActivity='.view.login.LoginActivity',
    noReset=True,
    newCommandTimeout=300,  
    autoGrantPermissions=True,
    ignoreHiddenApiPolicyError=True, # in Physical device, Script is throwing WRITE_SECURE_SETTINGS issue. Need to disable the policy and App monitoring
                                     # https://stackoverflow.com/questions/57018088
)

appium_server_url = 'http://localhost:4723'

class TestLogin(unittest.TestCase):
    def setUp(self) -> None:
        print("Initializing Appium driver...")
    
        self.driver = webdriver.Remote(
            appium_server_url,
            options=UiAutomator2Options().load_capabilities(capabilities)
        )
        self.wait = WebDriverWait(self.driver, 60)
        print("Appium driver initialized successfully")
        time.sleep(10)
        print(f"Current activity: {self.driver.current_activity}")

    def tearDown(self) -> None:
        if self.driver:
            self.driver.quit()
            print("Driver closed successfully")

    def test_login_success(self) -> None:
        """Test the login functionality"""
        try:
            current_activity = self.driver.current_activity
            print(f"Current activity: {current_activity}")
            self.assertTrue('.view.login.LoginActivity' in current_activity, 
                          f"Not on login activity. Current activity: {current_activity}")

            print("Looking for logo element...")
            logo = self.wait.until(
                EC.presence_of_element_located((AppiumBy.ID, 'com.sdapps.auraascend:id/logo'))
            )
            self.assertTrue(logo.is_displayed(), "Logo is not visible")
            print("Logo is visible")

            print("Looking for tagline element...")
            tagline = self.wait.until(
                EC.presence_of_element_located((AppiumBy.ID, 'com.sdapps.auraascend:id/tagLine'))
            )
            self.assertTrue(tagline.is_displayed(), "Tagline is not visible")
            print("Tagline is visible")


            print("Looking for email field...")
            email_field = self.wait.until(
                EC.presence_of_element_located((AppiumBy.ID, 'com.sdapps.auraascend:id/etEmail'))
            )
            print("Found email field")
            

            print("Looking for password field...")
            password_field = self.wait.until(
                EC.presence_of_element_located((AppiumBy.ID, 'com.sdapps.auraascend:id/etPassword'))
            )
            print("Found password field")
            
            print("Looking for login button...")
            login_button = self.wait.until(
                EC.presence_of_element_located((AppiumBy.ID, 'com.sdapps.auraascend:id/loginBtn'))
            )
            print("Found login button")


            email_field.send_keys("test@gmail.com")
            print("Entered email")
            password_field.send_keys("test123456")
            print("Entered password")
            

            login_button.click()
            print("Clicked login button")


            print("Waiting for successful login...")
            success_element = self.wait.until(
                EC.presence_of_element_located((AppiumBy.ID, 'com.sdapps.auraascend:id/myDaylabel'))
            )
            self.assertTrue(success_element.is_displayed(), "Login was not successful")
            print("Login successful")

        except Exception as e:
            print(f"Error occurred: {str(e)}")

if __name__ == '__main__':
    unittest.main()

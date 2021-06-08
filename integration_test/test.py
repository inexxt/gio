import unittest
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from time import sleep
import datetime

def set_date(driver, date):
    e = driver.find_element_by_tag_name("vaadin-date-picker")
    e.click()
    for _ in range(10):
        e.send_keys(Keys.BACKSPACE)

    e.send_keys(date) # date in a format M/D/YYYY, e.g. 1/13/2021
    e.send_keys(Keys.ENTER)

def set_time(driver, time):
    e = driver.find_element_by_tag_name("vaadin-time-picker")
    e.click()
    for _ in range(10):
        e.send_keys(Keys.BACKSPACE)

    e.send_keys(time) # time in a format HH:MM XX, e.g. 12:00 AM
    e.send_keys(Keys.ENTER)

def create_event(driver, time, date, name, desc, tags, guests):
    driver.find_element_by_link_text("Add event").click()
    sleep(2)
    assert "New event" == driver.title

    set_time(time)
    set_date(date)
    sleep(2)

    elem = driver.find_element_by_xpath("//vaadin-text-area")
    elem.click()
    elem.send_keys(name)

    elem = driver.find_element_by_xpath("//vaadin-text-area[2]")
    elem.click()
    elem.send_keys(desc)

    elem = driver.find_element_by_xpath("//vaadin-horizontal-layout[8]/vaadin-text-area")
    elem.click()
    elem.send_keys(tags)

    elem = driver.find_element_by_xpath("//vaadin-horizontal-layout[8]/vaadin-text-area[2]")
    elem.click()
    elem.send_keys(guests)

    # Submit
    driver.find_element_by_xpath("//vaadin-button").click()


def create_reminder(driver, time, date, text):
    driver.find_element_by_link_text("Add reminder").click()
    sleep(2)
    assert "New reminder" == driver.title

    set_date(date)
    set_time(time)
    elem = driver.find_element_by_xpath("//vaadin-text-area")
    elem.click()
    elem.send_keys(name)

    # Submit
    driver.find_element_by_xpath("//vaadin-button").click()


def create_note(driver, date, name, desc, tags):
    driver.find_element_by_link_text("Add note").click()
    sleep(2)
    assert "New note" == driver.title

    set_date(date)

    elem = driver.find_element_by_xpath("//vaadin-text-area")
    elem.click()
    elem.send_keys(name)

    elem = driver.find_element_by_xpath("//vaadin-text-area[2]")
    elem.click()
    elem.send_keys(desc)

    elem = driver.find_element_by_xpath("//vaadin-horizontal-layout[5]/vaadin-text-area")
    elem.click()
    elem.send_keys(tags)

    # Submit
    driver.find_element_by_xpath("//vaadin-button").click()


class CalendarTests(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()

    def test_create_task_today(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "11:00 AM"
        d = datetime.datetime.today()
        date = f"{d.month}/{d.day}/{d.year}"
        name = "Event1"
        desc = "Desc1"
        tags = "t1,t2"
        guests = "jac.karwowski@gmail.com"

        create_event(driver, time, date, name, desc, tags, guests)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        self.assertIn("Calendar overview", driver.title)
        set_date(date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source
        assert time in driver.page_source
        assert guests in driver.page_source

        driver.find_element_by_link_text("Today overview").click()
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source
        assert time in driver.page_source
        assert guests in driver.page_source

    def test_create_task_tomorrow(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "03:00 PM"
        d = datetime.datetime.today() + datetime.timedelta(days=1)
        date = f"{d.month}/{d.day}/{d.year}"
        name = "Event2"
        desc = "Desc2"
        tags = "t2"
        guests = ""

        create_event(driver, time, date, name, desc, tags, guests)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        self.assertIn("Calendar overview", driver.title)
        set_date(date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source
        assert time in driver.page_source
        assert guests in driver.page_source

        driver.find_element_by_link_text("Today overview").click()
        assert name not in driver.page_source
        assert desc not in driver.page_source

    def test_create_reminder_today(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "11:59 PM"
        d = datetime.datetime.today()
        name = "Reminder1"
        create_reminder(driver, time, date, text)
        driver.find_element_by_link_text("Today overview").click()
        sleep(5)
        assert name in driver.page_source

    def test_create_reminder_tomorrow(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "11:59 PM"
        d = datetime.datetime.today() + datetime.timedelta(days=1)
        name = "Reminder2"
        create_reminder(driver, time, date, text)
        driver.find_element_by_link_text("Today overview").click()
        sleep(5)
        assert name not in driver.page_source

    def test_create_reminder_earlier(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "00:01 AM"
        d = datetime.datetime.today()
        name = "Reminder3"
        create_reminder(driver, time, date, text)
        driver.find_element_by_link_text("Today overview").click()
        sleep(5)
        assert name not in driver.page_source

    def test_create_note_today(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        d = datetime.datetime.today()
        name = "Note1"
        desc = "NoteDesc1"
        tags = "n1, n2"
        create_note(driver, date, name, desc, tags)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        set_date(date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source

    def test_create_note_tomorrow(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        d = datetime.datetime.today() + datetime.timedelta(days=1)
        name = "Note1"
        desc = "NoteDesc1"
        tags = "n1, n2"
        create_note(driver, date, name, desc, tags)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        set_date(date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source

    def tearDown(self):
        self.driver.close()

if __name__ == "__main__":
    unittest.main()
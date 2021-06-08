import unittest
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.options import Options
from time import sleep
import datetime
import os
import subprocess

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

    set_time(driver, time)
    set_date(driver, date)
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
    driver.find_element_by_xpath("//div/vaadin-button").click()


def create_reminder(driver, time, date, text):
    driver.find_element_by_link_text("Add reminder").click()
    sleep(2)
    assert "New reminder" == driver.title

    set_date(driver, date)
    set_time(driver, time)
    elem = driver.find_element_by_xpath("//vaadin-text-area")
    elem.click()
    elem.send_keys(text)

    # Submit
    driver.find_element_by_xpath("//div/vaadin-button").click()


def create_note(driver, date, name, desc, tags):
    driver.find_element_by_link_text("Add note").click()
    sleep(2)
    assert "New note" == driver.title

    set_date(driver, date)

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
    driver.find_element_by_xpath("//div/vaadin-button").click()

def create_task(driver, date, **values):
    driver.find_element_by_link_text("Add task").click()
    sleep(2)
    assert "New task" == driver.title

    set_date(driver, date)
    sleep(2)
    
    params = {
        "duration": "//vaadin-text-area",
        "minimal": "//vaadin-text-area[2]",
        "maximal": "//vaadin-text-area[3]",
        "name": "//vaadin-horizontal-layout[4]/vaadin-text-area",
        "desc": "//vaadin-horizontal-layout[4]/vaadin-text-area[2]",
        "reps": "//vaadin-horizontal-layout[6]/vaadin-text-area",
        "time_between_reps": "//vaadin-horizontal-layout[6]/vaadin-text-area[2]",
        "tags": "//vaadin-horizontal-layout[7]/vaadin-text-area"
    }
    
    for elem_name in params.keys():
        elem = driver.find_element_by_xpath(params[elem_name])
        elem.click()
        for _ in range(10):
            elem.send_keys(Keys.BACKSPACE)
        
        elem.send_keys(values[elem_name])
    
#     elem =  driver.find_element_by_xpath("//vaadin-select")
#     for _ in range(values["heuristic"] - 1):
#         elem.send_keys(Keys.DOWN)
#     sleep(1)
#     elem.send_keys(Keys.ENTER)
    
    # Submit
    
    driver.find_element_by_xpath("//div/vaadin-button").click()
    

class CalendarTests(unittest.TestCase):
    
    def test_create_event_today(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "11:00 AM"
        d = datetime.datetime.today()
        date = f"{d.month}/{d.day}/{d.year}"
        name = "Event1"
        desc = "Desc1"
        tags = "t1,t2"
        guests = ""

        create_event(driver, time, date, name, desc, tags, guests)
        sleep(3)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        assert "Calendar overview" == driver.title
        set_date(driver, date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source

        driver.find_element_by_link_text("Today overview").click()
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source

    def test_create_event_tomorrow(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "11:00 AM"
        d = datetime.datetime.today() + datetime.timedelta(days=1)
        date = f"{d.month}/{d.day}/{d.year}"
        name = "Event2"
        desc = "Desc2"
        tags = "t2"
        guests = ""

        create_event(driver, time, date, name, desc, tags, guests)
        sleep(3)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        assert "Calendar overview" == driver.title
        set_date(driver, date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source
        assert guests in driver.page_source

        driver.find_element_by_link_text("Today overview").click()
        sleep(2)
        assert f"Event {name} starting at" not in driver.page_source

    def test_create_reminder_today(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "11:00 PM"
        d = datetime.datetime.today()
        date = f"{d.month}/{d.day}/{d.year}"
        text = "Reminder1"
        create_reminder(driver, time, date, text)
        driver.find_element_by_link_text("Today overview").click()
        sleep(5)
        assert text in driver.page_source

    def test_create_reminder_tomorrow(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "11:00 PM"
        d = datetime.datetime.today() + datetime.timedelta(days=1)
        date = f"{d.month}/{d.day}/{d.year}"
        text = "Reminder2"
        create_reminder(driver, time, date, text)
        driver.find_element_by_link_text("Today overview").click()
        sleep(5)
        assert text not in driver.page_source

    def test_create_reminder_earlier(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        time = "00:01 AM"
        d = datetime.datetime.today()
        date = f"{d.month}/{d.day}/{d.year}"
        text = "Reminder3"
        create_reminder(driver, time, date, text)
        driver.find_element_by_link_text("Today overview").click()
        sleep(5)
        assert text not in driver.page_source

    def test_create_note_today(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        d = datetime.datetime.today()
        date = f"{d.month}/{d.day}/{d.year}"
        name = "Note1"
        desc = "NoteDesc1"
        tags = "n1, n2"
        create_note(driver, date, name, desc, tags)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        set_date(driver, date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source

    def test_create_note_tomorrow(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        d = datetime.datetime.today() + datetime.timedelta(days=1)
        date = f"{d.month}/{d.day}/{d.year}"
        name = "Note1"
        desc = "NoteDesc1"
        tags = "n1, n2"
        create_note(driver, date, name, desc, tags)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        set_date(driver, date)
        assert name in driver.page_source
        assert desc in driver.page_source
        assert tags in driver.page_source
        
    def test_create_task_one_day(self):
        driver = self.driver
        driver.get("http://localhost:8080/")
        sleep(3)
        params = {
            "duration": "4",
            "minimal": "1",
            "maximal": "5",
            "name": "Task1",
            "desc": "TaskDesc1",
            "reps": "1",
            "time_between_reps": "",
            "tags": "t3,t4",
            "heuristic": 1
        }
        d = datetime.datetime.today() + datetime.timedelta(days=1)
        date = f"{d.month}/{d.day}/{d.year}"
        create_task(self.driver, date, **params)
        sleep(2)
        driver.find_element_by_link_text("Calendar overview").click()
        sleep(5)
        d = datetime.datetime.today()
        today = f"{d.month}/{d.day}/{d.year}"
        set_date(driver, today)
        assert params["name"] in driver.page_source
        assert params["desc"] in driver.page_source

    def tearDown(self):
        self.driver.close()

    def setUp(self):
        options = Options()
        options.headless = True
        self.driver = webdriver.Chrome(options=options)

if __name__ == "__main__":
    unittest.main()
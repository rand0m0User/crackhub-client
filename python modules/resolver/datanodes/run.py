import os
import asyncio
import ctypes
import platform
from urllib.parse import urlparse
import argparse

import aiohttp
from bs4 import BeautifulSoup

if platform.system()=='Windows':
    asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())

async def get_rand_value(session, id, file_name):
    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "Cookie": f"lang=english; file_name={file_name}; file_code={id};",
        "Host": "datanodes.to",
        "Origin": "https://datanodes.to",
        "Referer": "https://datanodes.to/download",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
    }

    payload = {
        "op": "download1",
        "usr_login": "",
        "id": id, 
        "fname": file_name,
        "referer": "",
        "method_free": "Free Download >>"
    }

    async with session.post(
        "https://datanodes.to/download",
        data=payload,
        headers=headers,
        allow_redirects=False,
    ) as response:
        if response.status == 200:
            page_html = await response.text()
            soup = BeautifulSoup(page_html, "html.parser")

            download_tag = soup.find("download-countdown")
            return download_tag.get("rand")
        return None

async def get_download_link(session, download_url):
    parsed_url = urlparse(download_url)
    path_segments = parsed_url.path.split("/")

    file_code = path_segments[1].encode("latin-1", "ignore").decode("latin-1")
    file_name = path_segments[-1].encode("latin-1", "ignore").decode("latin-1")

    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "Cookie": f"lang=english; file_name={file_name}; file_code={file_code};",
        "Host": "datanodes.to",
        "Origin": "https://datanodes.to",
        "Referer": "https://datanodes.to/download",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
    }

    payload = {
        "op": "download2",
        "id": file_code,
        "rand": "",
        "referer": "https://datanodes.to/download",
        "method_free": "Free Download >>",
        "method_premium": "",
        "adblock_detected": ""
    }

    async with session.post(
        "https://datanodes.to/download",
        data=payload,
        headers=headers,
        allow_redirects=False,
    ) as response:
        if response.status == 302:
            return response.headers.get("Location")
        return None

async def process_link(url):
    async with aiohttp.ClientSession() as session:
        url = url.strip()
        if url:
            link = await get_download_link(session, url)
            return link

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("path", type=str, help="path help")
    args = parser.parse_args()
    asyncio.set_event_loop(asyncio.new_event_loop())
    download_link = asyncio.run(process_link(args.path))
    print(download_link)

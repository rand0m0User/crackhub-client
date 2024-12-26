import argparse

from kraken import Kraken


def kraken_download(path: str):
    k = Kraken()
    print(k.get_download_link(path))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("path", type=str, help="path help")
    args = parser.parse_args()
    kraken_download(args.path)


if __name__ == "__main__":
    main()

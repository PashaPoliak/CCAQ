from pypdf import PdfReader
import csv
import re
import os

def extract_cards(pdf_path, category):
    reader = PdfReader(pdf_path)
    cards = []

    for page in reader.pages:
        text = page.extract_text()
        if not text:
            continue

        text = re.sub(r'^Claude Certified Architect:[^\n]+\nStudy online at https?://[^\n]+\n', '', text, flags=re.MULTILINE)
        text = re.sub(r'^Claude Architect Certification:[^\n]+\nStudy online at https?://[^\n]+\n', '', text, flags=re.MULTILINE)

        lines = text.split('\n')

        buf = []

        for line in lines:
            line = line.strip()
            if not line:
                continue

            match = re.match(r'^(\d+)\.\s*(.*)', line)
            if match:
                if buf:
                    full = ' '.join(buf)
                    parts = re.split(r'\? ', full, maxsplit=1)
                    if len(parts) == 2:
                        cards.append({'question': parts[0].strip(), 'answer': parts[1].strip()[:500], 'category': category})
                buf = [match.group(2)] if match.group(2) else []
            else:
                buf.append(line)

        if buf:
            full = ' '.join(buf)
            parts = re.split(r'\? ', full, maxsplit=1)
            if len(parts) == 2:
                cards.append({'question': parts[0].strip(), 'answer': parts[1].strip()[:500], 'category': category})

    return cards

pdfs = [
    ('C:/dev/projects/fullstack/CCAQ/docs/1162066951_73.pdf', 'C:/dev/projects/fullstack/CCAQ/docs/fundamental.csv', 'fundamental'),
    ('C:/dev/projects/fullstack/CCAQ/docs/1166414598_132.pdf', 'C:/dev/projects/fullstack/CCAQ/docs/foundations.csv', 'foundations')
]

for pdf_path, csv_path, category in pdfs:
    cards = extract_cards(pdf_path, category)

    with open(csv_path, 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=['question', 'answer', 'category'])
        writer.writeheader()
        writer.writerows(cards)

    print(f'{csv_path}: {len(cards)} cards')


if __name__ == "__main__":
    from lxml import isoschematron, etree
    parsed = etree.parse('constraints.sch')
    schematron = isoschematron.Schematron(parsed, store_report=True)
    print(schematron)
    if(schematron.is_valid_schema()):
        print('schema valid')

    xml_data = etree.parse('scribble.xml')
    print(xml_data)
    print("abc")
    result = schematron.validate(xml_data)
    report = schematron.validation_report
    print("is valid" + str(result))
    print(type(report))
    print(report)
#     schematron = isoschematron.Schematron(etree.XML('''
# <schema xmlns="http://purl.oclc.org/dsdl/schematron" >
# <pattern id="id_only_attribute">
#      <title>id is the only permitted attribute name</title>
#      <rule context="*">
#        <report test="@*[not(name()='id')]">Attribute
#          <name path="@*[not(name()='id')]"/> is forbidden<name/>
#        </report>
#      </rule>
#    </pattern>
#  </schema>'''),
#     error_finder=isoschematron.Schematron.ASSERTS_AND_REPORTS)

#     xml = etree.XML('''
#  <AAA name="aaa">
#    <BBB id="bbb"/>
#   <CCC color="ccc"/>
#  </AAA>
# ''')

#     valid = schematron.validate(xml)
#     print(valid)

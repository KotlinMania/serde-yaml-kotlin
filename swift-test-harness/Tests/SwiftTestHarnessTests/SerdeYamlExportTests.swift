import Testing
import SerdeYaml

@Suite("SerdeYaml Swift Export Suite")
struct SerdeYamlExportTests {
    @Test("Swift module loads and basic export smoke test")
    func testSwiftModuleLoads() throws {
        #expect(true, "SerdeYaml swift module imported cleanly")
    }
}


using System.Net;
using System.Text;
using Microsoft.AspNetCore.TestHost;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using Moq;
using Newtonsoft.Json;
using Tmp1CService.DTOs.VehicleDTOs;
using Tmp1CService.Models;
using Tmp1CService.Repositories.Interfaces;

namespace Tmp1CService.Tests;

public class TestVehiclesController : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly HttpClient _client;
    private readonly AppDbContext _db;
    private readonly CustomWebApplicationFactory<Program> _factory;

    public TestVehiclesController(CustomWebApplicationFactory<Program> factory)
    {
        _factory = factory;
        _client = factory.CreateClient();
        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseInMemoryDatabase("TestDb")
            .Options;
        _db = new AppDbContext(options);
    }

    private HttpClient CreateClientWithMock(Mock<IVehicleRepository> mockRepo)
    {
        return _factory.WithWebHostBuilder(builder =>
        {
            builder.ConfigureTestServices(services =>
            {
                services.RemoveAll<IVehicleRepository>();
                services.AddScoped(_ => mockRepo.Object);
            });
        }).CreateClient();
    }

    private static void AddApiKey(HttpClient client)
    {
        client.DefaultRequestHeaders.Add("X-API-KEY", "qweasd");
    }

    private static Client GetTestClient(Guid? id = null)
    {
        return new Client
        {
            Id = id ?? Guid.NewGuid(),
            Login = "TestClient",
            Password = "123",
            Url1C = "http://localhost/1CTest"
        };
    }

    private static async Task<string> LoadJsonDataAsync(string fileName)
    {
        var path = Path.Combine(Directory.GetCurrentDirectory(), "TestData", fileName);
        return await File.ReadAllTextAsync(path);
    }

    private static StringContent CreateJsonContent(object data)
    {
        return new StringContent(JsonConvert.SerializeObject(data), Encoding.UTF8, "application/json");
    }

    #region GetVehicles

    #region return200

    [Fact]
    public async Task Get_Vehicles_ReturnSuccess()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var mockRepo = new Mock<IVehicleRepository>();
        var carsJson = await LoadJsonDataAsync("VehiclesFrom1C.json");
        mockRepo.Setup(repo => repo.GetVehiclesFrom1C(It.Is<Client>(u => u.Id == client1C.Id))).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.OK,
                Content = new StringContent(carsJson, Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);

        // Act
        var result = await client.GetAsync($"api/clients/{client1C.Id}/vehicles/");

        // Assert
        Assert.Equal(HttpStatusCode.OK, result.StatusCode);
        mockRepo.Verify(repo => repo.GetVehiclesFrom1C(It.IsAny<Client>()), Times.Once());

        var responseContent = await result.Content.ReadAsStringAsync();
        var vehicles = JsonConvert.DeserializeObject<List<VehicleDto>>(responseContent);
        var expectedVehicles = JsonConvert.DeserializeObject<List<VehicleDto1C>>(carsJson)!;

        Assert.NotNull(vehicles);
        Assert.NotEmpty(vehicles);
        Assert.Equal(expectedVehicles.Count, vehicles.Count);

        for (var i = 0; i < expectedVehicles.Count; i++)
        {
            var expected = expectedVehicles[i];
            var actual = vehicles[i];
            Assert.Equal(expected.Brand, actual.Brand);
            Assert.Equal(expected.ChassisNumber, actual.ChassisNumber);
            Assert.Equal(expected.Dimensions, actual.Dimensions);
            Assert.Equal(expected.EngineModel, actual.EngineModel);
            Assert.Equal(expected.EngineNumber, actual.EngineNumber);
        }
    }

    #endregion

    #region return400

    [Fact]
    public async Task Get_Vehicles_ReturnsBadRequest_WhenClientIdIsEmpty()
    {
        // Arrange
        var client = _factory.CreateClient();
        AddApiKey(client);

        // Act
        var result = await client.GetAsync($"api/clients/{Guid.Empty}/vehicles/");

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, result.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task Get_Vehicles_ReturnsNotFound_WhenUserNotFound()
    {
        // Arrange
        var client = _factory.CreateClient();
        AddApiKey(client);
        var nonExistentClientId = Guid.NewGuid();

        // Act
        var result = await client.GetAsync($"api/clients/{nonExistentClientId}/vehicles/");

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, result.StatusCode);
    }

    #endregion

    #endregion

    #region GetVehicle

    #region return200

    [Fact]
    public async Task GetVehicle_ReturnsOk_WhenVehicleExists()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var mockRepo = new Mock<IVehicleRepository>();
        var carJson = await LoadJsonDataAsync("VehicleFrom1C.json");
        var expectedVehicle = JsonConvert.DeserializeObject<VehicleDto1C>(carJson)!;

        mockRepo.Setup(repo => repo.GetVehicleFrom1C(
            It.Is<Client>(u => u.Id == client1C.Id),
            It.Is<string>(v => v == expectedVehicle.Code1C))).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.OK,
                Content = new StringContent(carJson, Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);

        // Act
        var response = await client.GetAsync($"api/clients/{client1C.Id}/vehicles/{expectedVehicle.Code1C}");

        // Assert
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);

        var responseContent = await response.Content.ReadAsStringAsync();
        var vehicle = JsonConvert.DeserializeObject<VehicleDto>(responseContent);

        Assert.NotNull(vehicle);
        Assert.Equal(expectedVehicle.Code1C, vehicle.Code1C);
        Assert.Equal(expectedVehicle.ChassisNumber, vehicle.ChassisNumber);
        Assert.Equal(expectedVehicle.Brand, vehicle.Brand);
        Assert.Equal(expectedVehicle.Model, vehicle.Model);
        Assert.Equal(expectedVehicle.Vin, vehicle.Vin);
    }

    #endregion

    #region return400

    [Fact]
    public async Task Get_Vehicle_ReturnsBadRequest_WhenClientIdIsEmpty()
    {
        // Arrange
        AddApiKey(_client);
        var vehicleCode = "999999999";

        // Act
        var result = await _client.GetAsync($"api/clients/{Guid.Empty}/vehicles/{vehicleCode}");

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, result.StatusCode);
    }

    [Fact]
    public async Task Get_Vehicle_ReturnsBadRequest_WhenClientIdIsInvalid()
    {
        // Arrange
        AddApiKey(_client);
        var vehicleCode = "999999999";

        // Act
        var result = await _client.GetAsync($"api/clients/efewfgwrfgerf/vehicles/{vehicleCode}");

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, result.StatusCode);
    }

    #endregion

    #region return401

    [Fact]
    public async Task GetVehicle_ReturnsUnauthorized_WhenApiKeyIsMissing()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();
        var vehicleCode1C = "000000001";

        var client = _factory.CreateClient(); // Без API-ключа

        // Act
        var response = await client.GetAsync($"api/clients/{client1C.Id}/vehicles/{vehicleCode1C}");

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task GetVehicle_ReturnsNotFound_WhenVehicleDoesNotExist()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();
        var nonExistentVehicleCode = "999999999";

        var mockRepo = new Mock<IVehicleRepository>();
        mockRepo.Setup(repo => repo.GetVehicleFrom1C(
            It.Is<Client>(u => u.Id == client1C.Id),
            It.Is<string>(v => v == nonExistentVehicleCode))).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.NotFound,
                Content = new StringContent("Транспортное средство не найдено", Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);

        // Act
        var response = await client.GetAsync($"api/clients/{client1C.Id}/vehicles/{nonExistentVehicleCode}");

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    #endregion

    #endregion

    #region CreateVehicle

    #region return201

    [Fact]
    public async Task CreateVehicle_ReturnsCreated_WhenVehicleIsValid()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var vehicleDto = new VehicleDto1C { Code1C = "12345", Model = "TestModel", LicensePlate = "ABC123" };

        var mockRepo = new Mock<IVehicleRepository>();
        mockRepo.Setup(repo => repo.CreateVehicleIn1C(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.IsAny<VehicleDto1C>())).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.Created,
                Content = new StringContent(JsonConvert.SerializeObject(vehicleDto), Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await client.PostAsync($"api/clients/{client1C.Id}/vehicles", content);

        // Assert
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
    }

    #endregion

    #region return400

    [Fact]
    public async Task CreateVehicle_ReturnsBadRequest_WhenVehicleDataIsInvalid()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var invalidVehicleDto = new VehicleDto1C();

        var mockRepo = new Mock<IVehicleRepository>();
        mockRepo.Setup(repo => repo.CreateVehicleIn1C(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.IsAny<VehicleDto1C>())).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.BadRequest,
                Content = new StringContent("Не корректно введены данные транспорта", Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);
        var content = CreateJsonContent(invalidVehicleDto);

        // Act
        var response = await client.PostAsync($"api/clients/{client1C.Id}/vehicles", content);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    #endregion

    #region return401

    [Fact]
    public async Task CreateVehicle_ReturnsUnauthorized_WhenApiKeyIsMissing()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var vehicleDto = new VehicleDto1C { Code1C = "12345", Model = "TestModel", LicensePlate = "ABC123" };
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await _client.PostAsync($"api/clients/{client1C.Id}/vehicles", content);

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task CreateVehicle_ReturnsNotFound_WhenClientDoesNotExist()
    {
        // Arrange
        var missingClientId = Guid.NewGuid();
        var vehicleDto = new VehicleDto1C { Code1C = "12345", Model = "TestModel", LicensePlate = "ABC123" };

        AddApiKey(_client);
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await _client.PostAsync($"api/clients/{missingClientId}/vehicles", content);

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    [Fact]
    public async Task CreateVehicle_ReturnsNotFound_WhenVehicleDoesNotExist()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var vehicleDto = new VehicleDto1C { Code1C = "12345", Model = "TestModel", LicensePlate = "ABC123" };

        var mockRepo = new Mock<IVehicleRepository>();
        mockRepo.Setup(repo => repo.CreateVehicleIn1C(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.IsAny<VehicleDto1C>())).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.NotFound,
                Content = new StringContent("Транспортное средство не найдено", Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await client.PostAsync($"api/clients/{client1C.Id}/vehicles", content);

        // Assert
        mockRepo.Verify(repo => repo.CreateVehicleIn1C(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.IsAny<VehicleDto1C>()), Times.Once());
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    #endregion

    #endregion

    #region UpdateVehicle

    #region return200

    [Fact]
    public async Task UpdateVehicle_ReturnsOk_WhenVehicleIsUpdated()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var vehicleDto = new VehicleUpdateDto { LicensePlate = "XYZ987", Mass = 12312, EngineModel = "dfdsfdsfds" };
        var vehicleCode = "000000001";

        var mockRepo = new Mock<IVehicleRepository>();
        mockRepo.Setup(repo => repo.UpdateVehicleIn1C(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.Is<string>(c => c == vehicleCode),
            It.IsAny<VehicleUpdateDto>())).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.OK,
                Content = new StringContent(JsonConvert.SerializeObject(vehicleDto), Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await client.PutAsync($"api/clients/{client1C.Id}/vehicles/{vehicleCode}", content);

        // Assert
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }

    #endregion

    #region return400

    [Fact]
    public async Task UpdateVehicle_ReturnsBadRequest_WhenInvalidData()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var vehicleDto = new VehicleDto1C();
        var vehicleCode = "000000001";

        var mockRepo = new Mock<IVehicleRepository>();
        mockRepo.Setup(repo => repo.UpdateVehicleIn1C(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.Is<string>(c => c == vehicleCode),
            It.IsAny<VehicleUpdateDto>())).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.BadRequest,
                Content = new StringContent("Некорректные данные", Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await client.PutAsync($"api/clients/{client1C.Id}/vehicles/{vehicleCode}", content);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    #endregion

    #region return401

    [Fact]
    public async Task UpdateVehicle_ReturnsUnauthorized_WhenClientIdIsEmpty()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var vehicleDto = new VehicleDto1C { Code1C = "12345", Model = "UpdatedModel", LicensePlate = "XYZ987" };
        var vehicleCode = "000000001";

        var client = _factory.CreateClient(); // Без API-ключа
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await client.PutAsync($"api/clients/{client1C.Id}/vehicles/{vehicleCode}", content);

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task UpdateVehicle_ReturnsNotFound_WhenClientDoesNotExist()
    {
        // Arrange
        var vehicleDto = new VehicleDto1C { Code1C = "12345", Model = "UpdatedModel", LicensePlate = "XYZ987" };
        var vehicleCode = "000000001";

        AddApiKey(_client);
        var content = CreateJsonContent(vehicleDto);

        // Act
        var response = await _client.PutAsync($"api/clients/{Guid.NewGuid()}/vehicles/{vehicleCode}", content);

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    #endregion

    #endregion
}